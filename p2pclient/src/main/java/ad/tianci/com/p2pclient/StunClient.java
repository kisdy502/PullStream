package ad.tianci.com.p2pclient;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import de.javawi.jstun.attribute.*;
import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.header.MessageHeaderParsingException;
import de.javawi.jstun.util.Address;
import de.javawi.jstun.util.UtilityException;

public class StunClient {
    public static final String TAG = "StunClient";

    public interface StunClientListener {
        public void onMappedAddressReceived(MappedAddress mappedAddress);

        public void onRemoteDeviceRequestReceived(MappedAddress mappedAddress);

        public void onHolePunchingSucceed();

        public void onMessageReceived(String msg);
    }

    public static synchronized StunClient getInstance() {
        if (mInstance == null) {
            mInstance = new StunClient();
        }

        return mInstance;
    }

    public void close() {
        if (mSocket != null) {
            mSocket.close();
        }
        mInstance = null;
    }

    public void setStunClientListener(StunClientListener listenr) {
        mListener = listenr;
    }

    public void requestMappedAddress(String stunServerAddress, int stunServerPort) {
        while (!mSocket.isClosed() && mNeedRefreshMappedAddress) {
            try {
                MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
                sendMH.generateTransactionID();
                sendMH.addMessageAttribute(new ChangeRequest());

                byte[] data = sendMH.getBytes();
                DatagramPacket bindingMsg = new DatagramPacket(data, data.length, InetAddress.getByName(stunServerAddress), stunServerPort);
                mSocket.send(bindingMsg);

                Log.d(TAG, "Binding Request sent.");
                Thread.sleep(500);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UtilityException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void connectToRemoteDevice(Device remoteDevice, String stunServerAddress, int stunServerPort) {
        while (!mSocket.isClosed() && !mIsHolePunchingSuccessful) {
            Log.d(TAG, "Start to connect " + remoteDevice.toString());

            try {
                // Send any data to remote device to open the communication channel.
                String someData = "hello";
                DatagramPacket dp = new DatagramPacket(someData.getBytes(), someData.getBytes().length, InetAddress.getByName(remoteDevice.ip), remoteDevice.port);
                mSocket.send(dp);
                mSocket.send(dp);
                mSocket.send(dp);
                Log.d(TAG, "Send test message to device " + remoteDevice.ip + " : " + remoteDevice.port);

                // Ask STUN server send binding response to the remote device.
                // When remote device received the binding response, it will send data back to open
                // the communication channel.
                // Then the device can comminication with the remote device.
                ResponseAddress ra = new ResponseAddress();
                ra.setAddress(new Address(remoteDevice.ip));
                ra.setPort(remoteDevice.port);

                MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
                sendMH.generateTransactionID();
                sendMH.addMessageAttribute(ra);
                sendMH.addMessageAttribute(new ChangeRequest());

                byte[] data = sendMH.getBytes();
                DatagramPacket bindingMsg = new DatagramPacket(data, data.length, InetAddress.getByName(stunServerAddress), stunServerPort);
                mSocket.send(bindingMsg);
                Log.d(TAG, "Binding Request with RESPONSE_ADDRESS sent.");

                Thread.sleep(1000);
            } catch (UtilityException e) {
                e.printStackTrace();
            } catch (MessageAttributeException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mIsHolePunchingSuccessful = false;
    }

    public void testP2P(Device remoteDevice, String msg) {
        if (!mSocket.isClosed()) {
            try {
                String testMsg = "test|" + msg;
                DatagramPacket dp = new DatagramPacket(testMsg.getBytes(), testMsg.getBytes().length, InetAddress.getByName(remoteDevice.ip), remoteDevice.port);
                mSocket.send(dp);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private StunClient() {
        try {
            mSocket = new DatagramSocket();
            mSocket.setReuseAddress(true);
            mSocket.setSoTimeout(0);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        new PunchingRequestListeningThread().start();
    }

    private class PunchingRequestListeningThread extends Thread {

        @Override
        public void run() {
            while (!mSocket.isClosed()) {
                try {
                    Log.d(TAG, "PunchRequestListeningThread started.");
                    DatagramPacket responseMsg = new DatagramPacket(new byte[200], 200);
                    mSocket.receive(responseMsg);

                    String content = new String(responseMsg.getData());
                    Log.d(TAG, "content:" + content);
                    if (content.startsWith("ok")) {
                        Log.d(TAG, "P2P communication OK");
                        mIsHolePunchingSuccessful = true;
                        if (mListener != null) {
                            mListener.onHolePunchingSucceed();
                        }
                    } else if (content.startsWith("test|")) {
                        if (mListener != null) {
                            mListener.onMessageReceived(content.substring(5));
                        }
                    } else {
                        Log.i(TAG, "parseAttributes...");
                        MessageHeader receiveMH = MessageHeader.parseHeader(responseMsg.getData());
                        receiveMH.parseAttributes(responseMsg.getData());

                        MappedAddress mappedAddress = (MappedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
                        ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
                        if (ec != null) {
                            Log.d(TAG, "Errorcode : " + ec.getResponseCode() + ", reason : " + ec.getReason());
                            continue;
                        }

                        if (mappedAddress == null) {
                            Log.d(TAG, "Response does not contain a Mapped Address.");
                        } else {
                            Log.d(TAG, "mappedAddress = " + mappedAddress.toString());

                            if (mMappedAddress == null) { // Get mapped address of this device.
                                mNeedRefreshMappedAddress = false;
                                mMappedAddress = mappedAddress;

                                Log.d(TAG, "Get mapped address of this device");
                                if (mListener != null) {
                                    mListener.onMappedAddressReceived(mMappedAddress);
                                }
                            } else if (!mMappedAddress.getAddress().getInetAddress().getHostAddress().equals(mappedAddress.getAddress().getInetAddress().getHostAddress())
                                    || mMappedAddress.getPort() != mappedAddress.getPort()) { // Remote device wants to communicate with this device.
                                Log.d(TAG, "Remote device wants to communicate with this device");

                                if (mListener != null) {
                                    mListener.onRemoteDeviceRequestReceived(mMappedAddress);
                                }

                                // Send back any data to remote device to open the communication channel.
                                Log.d(TAG, "Send test message to device " + mappedAddress.toString());
                                String someData = "ok";
                                mSocket.send(new DatagramPacket(someData.getBytes(), someData.getBytes().length, mappedAddress.getAddress().getInetAddress(), mappedAddress.getPort()));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MessageHeaderParsingException e) {
                    e.printStackTrace();
                } catch (MessageAttributeParsingException e) {
                    e.printStackTrace();
                } catch (UtilityException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "PunchingRequestListeningThread finished");
        }

        private MappedAddress mMappedAddress;
    }

    private static StunClient mInstance;

    private DatagramSocket mSocket;
    private boolean mNeedRefreshMappedAddress = true;
    private boolean mIsHolePunchingSuccessful = false;
    private StunClientListener mListener;
}
