package com.sparrowwallet.hummingbird.registry.near;

import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import java.util.ArrayList;
import java.util.List;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;

public class NearSignature extends RegistryItem {
    public static final int REQUEST_ID = 1;
    public static final int SIGNATURE = 2;

    private final byte[] requestId;
    private final List<byte[]> signatureList;

    public NearSignature(List<byte[]> signatureList) {
        this.requestId = null;
        this.signatureList = signatureList;
    }

    public NearSignature(List<byte[]> signatureList, byte[] requestId) {
        this.requestId = requestId;
        this.signatureList = signatureList;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public List<byte[]> getSignatureList() {
        return signatureList;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.NEAR_SIGNATURE;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if(requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }
        Array array = new Array();
        for (byte[] signature: signatureList) {
            DataItem x = new ByteString(signature);
            array.add(x);
        }
        map.put(new UnsignedInteger(SIGNATURE), array);
        return map;
    }

    public static NearSignature fromCbor(DataItem item) {
        byte[] requestId = null;
        List<byte[]> signatureList = null;

        Map map = (Map)item;
        for(DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGNATURE) {
                Array signatureArray = (Array) map.get(uintKey);
                signatureList = new ArrayList<>(signatureArray.getDataItems().size());
                for (DataItem signData : signatureArray.getDataItems()) {
                    signatureList.add(((ByteString)signData).getBytes());
                }
            }
        }

        if(signatureList == null) {
            throw new IllegalStateException("required data field is missing");
        }

        if(requestId != null) {
            return new NearSignature(signatureList, requestId);
        } else {
            return new NearSignature(signatureList);
        }
    }
}