/*
 * This file is part of JSTUN. 
 * 
 * Copyright (c) 2005 Thomas King <king@t-king.de> - All rights
 * reserved.
 * 
 * This software is licensed under either the GNU Public License (GPL),
 * or the Apache 2.0 license. Copies of both license agreements are
 * included in this distribution.
 */

package de.javawi.jstun.attribute;

import android.util.Log;

public class ChangedAddress extends MappedResponseChangedSourceAddressReflectedFrom {
    public static final String TAG = "ChangedAddress";

	public ChangedAddress() {
		super(MessageAttribute.MessageAttributeType.ChangedAddress);
	}
	
	public static MessageAttribute parse(byte[] data) throws MessageAttributeParsingException {
		ChangedAddress ca = new ChangedAddress();
		MappedResponseChangedSourceAddressReflectedFrom.parse(ca, data);
		Log.d(TAG, "Message Attribute: Changed Address parsed: " + ca.toString() + ".");
		return ca;
	}
}
