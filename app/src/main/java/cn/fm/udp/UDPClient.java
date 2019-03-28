package cn.fm.udp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UDPClient {

	private static Logger LogWriter = Logger.getLogger(UDPClient.class.getName());
	public static ExecutorService pool = Executors.newCachedThreadPool();
	public static long threadTime = 0;
	// 是否正在下载
	public boolean isDownLoad = false;
	// 是否正在提供下载
	public boolean isSource = false;
	public long filelength = 0;
	private Callback callback;
	// 协议代码长度
	private static final int code_length = 3;
	// 文件head长度
	private static final int data_length = 5;
	private static final int head_length = code_length + data_length;
	// 文件传输字节
	private static final int byte_file_length = 1024 * 1;
	// udp Server服务器地址
	private String serverHost;
	// 提供/下载文件
	private String files;
	// 源文件/下载目录
	private String dir;
	// 文件可分为fragmentCount个碎片
	private long fragmentCount = 0;
	private DatagramSocket sclient = null;
	// 重连失败次数
	private int reConnent = 0;
	// 需要下载的碎片段
	private List<String> nodes = null;
	// 碎片段最多200个
	private long basesize = 200;
	// nat类型
	private static int natType = 0;

	public void startClient(String serverHost,String dir, int code,String files,Callback callback) {
		try {
			// 检测nat类型
			if(natType == 0) {
				natType = testNet(serverHost);
			}
			if(callback != null) {
				this.callback = callback;
			}
			callback.callback(Constant.CALL_BACK_NAT_TYPE, String.valueOf(natType));
			this.serverHost = serverHost;
			this.files = files;
			this.dir = dir;
			sclient = new DatagramSocket();
			String message = code + (natType + files);
			LogWriter.info("1. 连接服务器 目标：" + serverHost + ":" + 2008 + "内容：" + message);
			sendMessage(message, serverHost, 2008, sclient);
			if(code == Constant.SC_LOGIN) {
				// A 不断接收来自S/B端的消息
				isSource = false;
				receive(sclient,5000,true);
			}else if(code == Constant.SC_DOWNLOAD) {
				if(isDownLoad) {
					return;
				}
				try {
					byte[] buf = new byte[1024];
					DatagramPacket recpack = new DatagramPacket(buf, buf.length);
					sclient.setSoTimeout(5000);
					sclient.receive(recpack);
					String receiveMessage = new String(recpack.getData(), "UTF-8").trim();
					int returncode = Integer.valueOf(receiveMessage.substring(0, code_length));
					receiveMessage = receiveMessage.substring(code_length);
					if (returncode == Constant.SC_SLIST) {
						if(receiveMessage == null || receiveMessage.equals("")) {
							LogWriter.info("没有可下载的源，直接去服务器下载!");
							callback.callback(Constant.CALL_BACK_NO_SOURCE,files);
							return;
						}
						// B收到源列表，重复向源打洞
						LogWriter.info("接收源列表： " + receiveMessage);
						String[] hosts = receiveMessage.trim().split(",");
						sendMessaage2A(hosts, sclient);
					}else {
						LogWriter.info("收到协议：" + returncode);
						sclient.receive(recpack);
					}
				} catch (Exception e) {
					LogWriter.info("接收服务器下发源超时,"+e.getMessage());
					reConnent ++;
					if(reConnent < 5) {
						startClient(serverHost, dir, code, files, callback);
					}else {
						callback.callback(Constant.CALL_BACK_FAIL,files);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param server1Host
	 * @return
	 *   return 0 检测失败
	 *   return 1 全锥型NAT
	 *   return 2 限制锥型NAT
	 *   return 3端口 限制锥型NAT
	 *   return 4 对称型NAT
	 */
	private int testNet(String server1Host) {
		try {
			String server2Host = null;
			int server2port1 = 0;
			DatagramSocket client = new DatagramSocket();
			//向server1发送数据.将会受到server1和server2的回包
			sendMessage(String.valueOf(Constant.SC_NAT_TYPE), server1Host, 2008, client);
			
			String server1ReturnNAT = null;
			String server2ReturnNAT = null;
			for (int i = 0; i < 2; i++) {
				byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					client.setSoTimeout(5000);
					client.receive(packet);
					byte[] content = packet.getData();
					int code = getCode(content);
					String receiveMessage = new String(Arrays.copyOfRange(content, code_length, content.length), "UTF-8").trim();
					if (code == Constant.SC_NAT_TYPE_BACK) {
						if (receiveMessage.contains(",")) {
							//返回两个地址，一个是客户端的外网NET，另外一个是服务器2的地址
							String[] receiveMessageArr = receiveMessage.split(",");
							server1ReturnNAT = receiveMessageArr[0];
							String[] server2Arr = receiveMessageArr[1].split(":");
							server2Host = server2Arr[0];
							server2port1 = Integer.valueOf(server2Arr[1]);
						} else {
							server2ReturnNAT = receiveMessage;
						}
					}
				} catch (Exception e) {
					LogWriter.info("接收服务器下发超时,"+e.getMessage());
				}
			}
			
			if (server1ReturnNAT == null) {
				LogWriter.info("server1 链接不上");
				return 0;
			}
			
			//向server1发包，能收到server1和server2的回包，是完全锥形 NAT
			if (server2ReturnNAT != null) {
				LogWriter.info("server1ReturnNAT："+ server1ReturnNAT + ",server2ReturnNAT：" + server2ReturnNAT);
				return 1;
			}
			
			//向server2发送数据.将会受到server2的port1和port2的回包
			sendMessage(String.valueOf(Constant.SC_NAT_TYPE), server2Host, server2port1, client);
			Integer server2port2 = null;  
			for (int i = 0; i < 2; i++) {
				try {
					byte[] buf = new byte[1024];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					client.setSoTimeout(5000);
					client.receive(packet);
					byte[] content = packet.getData();
					int code = getCode(content);
					String receiveMessage = new String(Arrays.copyOfRange(content, code_length, content.length), "UTF-8").trim();
					if (code == 201) {
						if (server2port1 != packet.getPort()) {
							server2port2 = packet.getPort();
						} else {
							server2ReturnNAT = receiveMessage;
						}
					}
				} catch (Exception e) {
					LogWriter.info("接收服务器下发超时,"+e.getMessage());
				}
			}
			
			LogWriter.info("server2 port1："+ server2port1 + ",port2：" + server2port2);
			LogWriter.info("server1ReturnNAT："+ server1ReturnNAT + ",server2ReturnNAT：" + server2ReturnNAT);
			
			if (server2ReturnNAT == null) {
				LogWriter.info("server2 链接不上");
				return 0;
			}
			
			//向server2的port1发包，能收到server2的port1和port2的回包，是限制锥形 NAT， IP 限制，Port不限制
			if (server2port2 != null) {
				return 2;
			}
			
			String[] server1ReturnNATArr = server1ReturnNAT.split(":");
			String[] server2ReturnNATArr = server2ReturnNAT.split(":");
			
			//由server1回包的NAT端口和server2回包的NAT端口一致，是端口限制锥形 NAT。IP+Port 限制
			if (server1ReturnNATArr[1].equals(server2ReturnNATArr[1])) {
				return 3;
			}
			//以上都不是，就是对称型NET
			return 4;
		} catch (Exception e) {
			LogWriter.info("检测nat类型失败");
		}
		return -1;
	}
	/**
	 * 接收请求 （接收请求的回复,可能不是server回复的，有可能来自UPDClientB的请求内）
	 * @param restart: 是否需要重新心跳
	 * @param client
	 */
	private void receive(final DatagramSocket client, final int timeout, final boolean restart) {
		final DatagramSocket clientf = client;
		pool.execute(new Runnable() {
			@Override
			public void run() {
				byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					if(timeout != 0) {
						clientf.setSoTimeout(timeout);
					}
					clientf.receive(packet);
					byte[] content = packet.getData();
					int code = getCode(content);
					String receiveMessage = new String(Arrays.copyOfRange(content, code_length, content.length), "UTF-8").trim();
					if (code == Constant.SC_REP) {
						LogWriter.info("登录成功，接收回复消息，开始心跳 ");
						// 开启心跳线程，不断接收服务器消息
						heartbeat(clientf, serverHost);
					} else if (code == Constant.SC_SLIST) {
						if(receiveMessage == null || receiveMessage.equals("")) {
							LogWriter.info("下载地址为空，忽略");
							return;
						}
						if(isSource) {
							LogWriter.info("源正在提供下载，忽略其他下载");
							return;
						}
						// 收到B下载任务，停止心跳
						LogWriter.info("接收源列表： " + receiveMessage);
						threadTime = System.currentTimeMillis();
						isSource = true;
						String[] address = receiveMessage.split(":");
						sendMessaage2B(address[1], address[0], clientf);
					} else if(code == Constant.SC_NATB) {
						LogWriter.info("接收到B的打洞消息，穿透成功!地址："+packet.getAddress().getHostAddress()+":"+packet.getPort()+"需要下载文件："+receiveMessage);
						// 打洞成功发送文件大小
						byte[] filebyte = FileUtil.getBytes(dir+"/"+receiveMessage);
						byte[] sendBuf = (Constant.SC_FILE_SIZE+""+filebyte.length).getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendBuf,
								sendBuf.length, packet.getAddress(), packet.getPort());
						clientf.send(sendPacket);
						receive(clientf, 5*60*1000,true);
					}else if(code == Constant.SC_DOWNLOAD_READY) {
						// 收到准备消息，解析需要下发的碎片，开始不断下发文件
						InputStream inputstream = null;
						try {
							LogWriter.info("下发文件"+receiveMessage);
							String[] fileMessage = receiveMessage.split("::");
							String[] fragment = fileMessage[0].split("-");
							long startFragment = Long.valueOf(fragment[0]);
							long endFragment = Long.valueOf(fragment[1]);
							File source = new File(dir+"/"+fileMessage[1]);
							inputstream = new FileInputStream(source);
							inputstream.skip(startFragment*byte_file_length);
				            byte[] data = new byte[byte_file_length];
				            byte[] codeByte = (Constant.SC_FILE_CODE+"").getBytes();
				            //创建UDP数据报
				            int len;
				            long i = startFragment;
				            while ((len = inputstream.read(data)) != -1) {
				            	byte[] sendByte = new byte[head_length+data.length];
				            	// 把code装到sendByte里面，3个字节
				            	System.arraycopy(codeByte, 0, sendByte, 0, code_length);
				            	// 封装head byte数组
				            	byte[] head = new byte[data_length];
				            	System.arraycopy((i+"").getBytes(), 0, head, 0, (i+"").getBytes().length);
				            	// 把head装到sendByte里面，5个字节
				            	System.arraycopy(head, 0, sendByte, code_length, data_length);
				            	// 把文件数据装到sendByte里面
					            System.arraycopy(data, 0, sendByte, head_length, data.length);
				                DatagramPacket sendPacket = new DatagramPacket(sendByte, sendByte.length, packet.getAddress(), packet.getPort());
				                LogWriter.info("正在发送第"+i+"-"+len);
				                clientf.send(sendPacket);
				                i++;
				                if(i == endFragment) {
				                	TimeUnit.MILLISECONDS.sleep(5000);
				                	// 已经发送碎片断完毕，问下B端是否还有未发送完成的碎片
					                LogWriter.info(startFragment+"-"+endFragment+"段发送完成，请指示");
					                sendMessage(Constant.SC_DOWNLOAD_NODE_FINISH+"", packet.getAddress().getHostAddress(),packet.getPort(), clientf);
				            		break;
				            	}
				    			//限制传输速度 100毫秒
				                TimeUnit.MILLISECONDS.sleep(100);
				            }
				            // 下发结束，等待接收端完成下载的通知
				            receive(clientf, 2*60*1000,true);
//				            startClient(serverHost,dir, Constant.SC_LOGIN,files, callback);
						}finally {
							inputstream.close();
						}
					}else if(code == Constant.SC_DOWNLOAD_FINISH) {
						LogWriter.info("提供下载服务器完成!");
						startClient(serverHost,dir, Constant.SC_LOGIN,files, callback);
						callback.callback(Constant.CALL_BACK_RELOGIN,files);
					}else {
						LogWriter.info("收到数据包，code："+code+",地址："+packet.getAddress().getHostAddress()+":"+packet.getPort());
						receive(client,30000,true);
					}
				} catch (Exception e) {
					LogWriter.info("等待udp receive消息超时");
					if(restart) {
						startClient(serverHost,dir, Constant.SC_LOGIN,files, callback);
						callback.callback(Constant.CALL_BACK_RELOGIN,files);
					}
				}
			}
		});

	}
	/**
	 * 
	 * @param timeout： 接收byte超时时间
	 */
	private void down(DatagramSocket client ,final int timeout,final Set<String> natAddressList) {
		final DatagramSocket clientf = client;
		pool.execute(new Runnable() {
			@Override
			public void run() {
				byte[] buf = new byte[byte_file_length + head_length];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				Set<Integer> fragmentList = new HashSet<>();
				if(!new File(dir+"/temp/").exists()) {
					new File(dir+"/temp/").mkdirs();
				}
				String host = null;
				int port = 0;
				int failCount = 0 ;
				List<String> waitAddress = new ArrayList<>();
				while(isDownLoad) {
					FileOutputStream output = null;
					try {
						clientf.setSoTimeout(timeout);
						clientf.receive(packet);
						host = packet.getAddress().getHostAddress();
						port = packet.getPort();
						failCount = 0;
						byte[] content = packet.getData();
						int code = getCode(content);
						if(code == Constant.SC_FILE_CODE) {
							byte[] b = new byte[head_length];
							System.arraycopy(content, 0, b, 0, head_length);
							int fragmentI = Integer.valueOf(new String(b).trim().substring(code_length));
							byte[] fileByte = Arrays.copyOfRange(content, head_length, content.length);
							File dest = new File(dir+"/temp/"+fragmentI+"-"+files);
							output = new FileOutputStream(dest);
							// 最后一个碎片字节数不是byte_file_length
							if(fragmentI == fragmentCount-1) {
								int len = (int)(filelength - (byte_file_length*fragmentI));
								output.write(fileByte,0,len);
							}else {
								output.write(fileByte,0,byte_file_length);
							}
							output.flush();
							LogWriter.info("正在下载第"+fragmentI+"个碎片");
							// 需要检测当前的碎片是否全部下载完成
							fragmentList.add(fragmentI);
							// 碎片全部接收完毕，跳出循环
							if(fragmentList.size() == fragmentCount) {
								// 检查是否所有碎片都已经下载
								boolean isFinish = true;
								for (int i = 0; i < fragmentCount; i++) {
									if(!new File(dir+"/temp/"+i+"-"+files).exists()) {
										isFinish = false;
										break;
									}
								}
								synchronized (serverHost) {
									if(isFinish && isDownLoad) {
										// 拼接完整文件
										joinFile();
										for (String natAddress : natAddressList) {
											String[] address = natAddress.split(":");
											sendMessage(Constant.SC_DOWNLOAD_FINISH +"", address[0],Integer.valueOf(address[1]),clientf);
										}
									}
								}
								break;
							}
						}else if(code == Constant.SC_DOWNLOAD_NODE_FINISH) {
							synchronized (serverHost) {
								if(!nodes.isEmpty()) {
									LogWriter.info("断点下发："+nodes.get(0));
									sendMessage(Constant.SC_DOWNLOAD_READY +nodes.get(0)+"::"+ files, host,port, clientf);
									nodes.remove(0);
								}else {
									// 暂时没有碎片下发，进入等待列表
									waitAddress.add(host+":"+port);
								}
							}
						}
					} catch (Exception e) {
						LogWriter.info("下载文件socket等待超时");
						failCount++;
						// 失败次数大于2次
						if(failCount > 2) {
							isDownLoad = false;
							startClient(serverHost, dir, Constant.SC_DOWNLOAD, files, callback);
							break;
						}
						// 下载文件超时需要重新通知源端重新发送缺少的碎片
						if(nodes.isEmpty()) {
							nodes = getFileNode(fragmentList);
						}
						for (;;) {
							if(!nodes.isEmpty()&&!waitAddress.isEmpty()) {
								String[] address = waitAddress.get(0).split(":");
								LogWriter.info("需要补发碎片"+nodes.get(0));
								sendMessage(Constant.SC_DOWNLOAD_READY+nodes.get(0)+"::"+files, address[0],Integer.valueOf(address[1]), clientf);
								nodes.remove(0);
								waitAddress.remove(0);
							}else {
								break;
							}
								
						}
					}finally {
						try {
							if(output != null) {
								output.close();
							}
						} catch (IOException e) {
						}
					}
				}
			}
		});

	}
	/**
	 * A端收到S端下发的下载消息，发送打洞消息到B，为打洞做准备
	 */
	private void sendMessaage2B(String port, String host, DatagramSocket client) {
		try {
			LogWriter.info("向B发送数据(为NAT打孔做准备),NAT打洞：" + host + ":" + port);
			sendMessage(Constant.SC_NATA + "", host,Integer.parseInt(port), client);
			receive(client,30000,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * B端收到S端下发的下载消息，发送打洞消息到A(+需要下载的文件)
	 */
	private void sendMessaage2A(String[] hosts, DatagramSocket client) {
		int count = 3;
		Set<String> natAddressList = new HashSet<>();
		final DatagramSocket fclient = client;
		// 记录打洞成功列表和失败列表
		for (int j = 0; j < hosts.length; j++) {
			if(hosts[j].equals("")) {
				continue;
			}
			String[] address = hosts[j].split(":");
			// 轮询count次打洞
			int i = 0;
			for (;;) {
				try {
					LogWriter.info("向A发送数据,NAT打洞：" + hosts[j]);
					sendMessage(Constant.SC_NATB + files, address[0],Integer.valueOf(address[1]), fclient);
					// 等待接收UDPClientA回复的内容
					byte[] buf = new byte[1024];
					DatagramPacket recpack = new DatagramPacket(buf, buf.length);
					fclient.setSoTimeout(1000);
					fclient.receive(recpack);
					String receiveMessage = new String(recpack.getData(), "UTF-8").trim();
					int code = Integer.valueOf(receiveMessage.substring(0, code_length));
					receiveMessage = receiveMessage.substring(code_length);
					// B收到文件大小，回复A，我已经准备好了，开始下发文件吧
					if(code == Constant.SC_FILE_SIZE) {
						LogWriter.info("收到A的打洞回复!"+receiveMessage);
						if(filelength == 0) {
							filelength = Long.valueOf(receiveMessage);
						}
						// 能收到回复，说明打洞成功，加入到源列表中
						String reAddress = recpack.getAddress().getHostAddress()+":"+recpack.getPort();
						natAddressList.add(reAddress);
						if(reAddress.equals(hosts[j])) {
							break;
						}
					}else {
						LogWriter.info("收到A的消息!"+code+receiveMessage+","+recpack.getAddress().getHostAddress()+":"+recpack.getPort());
						sendMessage(Constant.SC_NATB + files, recpack.getAddress().getHostAddress(),recpack.getPort(), fclient);
					}
				} catch (Exception e) {
					LogWriter.info("第"+i+"次打洞失败,"+hosts[j]);
				}
				i++;
				if(i == count) {
					LogWriter.info("打洞失败，"+hosts[j]);
					callback.callback(Constant.CALL_BACK_NAT_FAIL,files);
					break;
				}
			}
		}
		if(!natAddressList.isEmpty()) {
			synchronized (serverHost) {
				if(!isDownLoad) {
					isDownLoad = true;
				}else {
					return;
				}
				fragmentCount();
				long startTime = System.currentTimeMillis();
				Set<Integer> fragmentList = new HashSet<>();
				File file = new File(dir+"/temp/");
				File[] fs = file.listFiles();
				if(fs != null) {
					// 遍历已经下载的碎片
					for(File f:fs){
						if(!f.isDirectory()&&f.getName().endsWith(files)) {
							fragmentList.add(Integer.valueOf(f.getName().replace("-"+files, "")));
						}
					}
				}
				LogWriter.info("已经下载："+fragmentList.size());
				if(fragmentList.size() == fragmentCount) {
					joinFile();
					return;
				}
				// fragmentList不为空说明已经下载过，需要补发漏掉的部分
				if(!fragmentList.isEmpty()) {
					nodes = getFileNode(fragmentList);
				}else {
					// 从打洞成功列表中分片下载文件
					nodes = getFileNode(natAddressList.size());
				}
				LogWriter.info("分片size："+nodes.size()+",过滤已经下载总共耗时："+(System.currentTimeMillis() - startTime));
				down(fclient,10000,natAddressList);
				for (String natAddress : natAddressList) {
					String[] address = natAddress.split(":");
					if(!nodes.isEmpty()) {
						LogWriter.info(natAddress+"下载"+nodes.get(0));
						sendMessage(Constant.SC_DOWNLOAD_READY +nodes.get(0)+"::"+ files, address[0],Integer.valueOf(address[1]),fclient);
						nodes.remove(0);
					}
				}
			}
		}else {
			callback.callback(Constant.CALL_BACK_FAIL,files);
		}
	}

	/**
	 * 心跳线程
	 * @param client
	 */
	private void heartbeat(DatagramSocket client,final String serverHost) {
		final long currTime = System.currentTimeMillis();
		threadTime = currTime;
		final DatagramSocket clientf = client;
		pool.execute(new Runnable() {
			@Override
			public void run() {
				while(threadTime == currTime) {
					try {
						sendMessage(Constant.SC_HEARTBEAT+"", serverHost,2008, clientf);
						receive(clientf, 100000,false);
						Thread.sleep(100000);
					} catch (Exception e) {
					}
				}
			}
		});
	}
	
	/**
	 * 发送消息
	 * @param sendMessage：消息内容
	 * @param address：发送地址
	 * @param port：发送端口
	 * @param client
	 */
	private static void sendMessage(String sendMessage, String address,int port, DatagramSocket client) {
		try {
			byte[] sendBuf = sendMessage.getBytes();
			SocketAddress target = new InetSocketAddress(address, port);
			DatagramPacket sendPacket = new DatagramPacket(sendBuf,sendBuf.length,target);
			client.send(sendPacket);
		} catch (Exception e) {
			LogWriter.info("发送消息失败:"+address+":"+port);
		}
	}
	/**
	 * 解析协议代码
	 * @return
	 */
	private int getCode(byte[] content) {
		byte[] b = new byte[code_length];
		System.arraycopy(content, 0, b, 0, code_length);
		return Integer.valueOf(new String(b));
	}
	/**
	 * 计算总碎片数
	 */
	private void fragmentCount() {
		// 计算总共拆分多少碎片
		if(filelength%byte_file_length == 0) {
			fragmentCount = filelength/byte_file_length;
		}else {
			fragmentCount = filelength/byte_file_length+1;
		}
		LogWriter.info("文件分成"+fragmentCount+"碎片");
	}
	/**
	 *  取断点碎片段
	 * @return
	 */
	private List<String> getFileNode(Set<Integer> fragmentList){
		List<String> nodeList = new ArrayList<>();
		// 封装需要补发的碎片列表
		int startNode = -1;
		for (int i = 0; i < fragmentCount; i++) {
			if(startNode == -1) {
				// 碎片从startNode开始
				if(!fragmentList.contains(i)) {
					startNode = i;
				}
			}else {
				if(fragmentList.contains(i)) {
					String node = startNode+"-"+i;
					LogWriter.info("add node:"+node);
					nodeList.add(node);
					startNode = -1;
				}else if(i == (fragmentCount-1)) {
					String node = startNode+"-"+fragmentCount;
					LogWriter.info("add node:"+node);
					nodeList.add(node);
				}else if(i-startNode > basesize) {
					String node = startNode+"-"+i;
					LogWriter.info("add node:"+node);
					nodeList.add(node);
					startNode = i;
				}
			}
		}
		return nodeList;
	}
	/**
	 * 获取文件碎片节点
	 * @param source:总共有source个源提供下载，文件可以分成source份下载
	 * @return
	 */
	private List<String> getFileNode(int source){
		// 划分各个源节点需要提供下载的碎片
		long count = fragmentCount/source;
		if(count < basesize) {
			basesize = count;
		}
		LogWriter.info("每个节点下载"+basesize+"个碎片");
		long basenode = 0;
		List<String> nodeList = new ArrayList<>();
		for(;;) {
			long startNode = basenode;
			basenode += basesize;
			long endNode = basenode ;
			if(endNode >= fragmentCount) {
				nodeList.add(startNode+"-"+fragmentCount);
				break;
			}
			nodeList.add(startNode+"-"+endNode);
		}
		return nodeList;
	}
	
	/**
	 * 碎片拼接
	 */
	private void joinFile() {
		FileOutputStream output = null;
		try {
			LogWriter.info("拼接碎片开始!");
			File dest = new File(dir+"/"+files);
			output = new FileOutputStream(dest);
			for (int i = 0; i < fragmentCount; i++) {
				byte[] fileByte = FileUtil.getBytes(dir+"/temp/"+i+"-"+files);
				output.write(fileByte,0,fileByte.length);
				output.flush();
				new File(dir+"/temp/"+i+"-"+files).delete();
			}
			callback.callback(Constant.CALL_BACK_SUCCESS,files);
			LogWriter.info("拼接碎片完成!");
		}catch (Exception e) {
		}finally {
			if(output != null) {
				try {
					output.close();
				} catch (IOException e) {
				}
			}
			isDownLoad = false;
			filelength = 0;
			fragmentCount = 0;
		}
	}
}
