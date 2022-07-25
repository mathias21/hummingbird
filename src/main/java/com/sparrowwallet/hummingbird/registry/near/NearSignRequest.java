package com.sparrowwallet.hummingbird.registry.near;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class NearSignRequest extends RegistryItem {
    public static final int REQUEST_ID = 1;
    public static final int SIGN_DATA = 2;
    public static final int DERIVATION_PATH = 3;
    public static final int ACCOUNT = 4;
    public static final int ORIGIN = 5;

    private final byte[] requestId;
    private final List<byte[]> signDataList;
    private final CryptoKeypath derivationPath;
    private final String account;
    private final String origin;


    public NearSignRequest(List<byte[]> signDataList, CryptoKeypath derivationPath) {
        this.requestId = null;
        this.signDataList = signDataList;
        this.derivationPath = derivationPath;
        this.account = null;
        this.origin = null;
    }


    public NearSignRequest(List<byte[]> signDataList, CryptoKeypath derivationPath, byte[] requestId) {
        this.requestId = requestId;
        this.signDataList = signDataList;
        this.derivationPath = derivationPath;
        this.account = null;
        this.origin = null;
    }


    public NearSignRequest(List<byte[]> signDataList, CryptoKeypath derivationPath, byte[] requestId, String account, String origin) {
        this.requestId = requestId;
        this.signDataList = signDataList;
        this.derivationPath = derivationPath;
        this.account = account;
        this.origin = origin;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public List<byte[]> getSignDataList() {
        return signDataList;
    }

    public String getDerivationPath() {
        return derivationPath.getPath();
    }

    public byte[] getMasterFingerprint() {
        return derivationPath.getSourceFingerprint();
    }

    public String getAccount() {
        return account;
    }

    public String getOrigin() {
        return origin;
    }


    @Override
    public String toString() {
        return "NearSignRequest{" +
                "requestId=" + Arrays.toString(requestId) +
                ", signDataList=" + signDataList +
                ", derivationPath=" + derivationPath +
                ", account='" + account + '\'' +
                ", origin='" + origin + '\'' +
                '}';
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.NEAR_SIGN_REQUEST;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }

        if (account != null) {
            map.put(new UnsignedInteger(ACCOUNT), new UnicodeString(account));
        }

        if (origin != null) {
            map.put(new UnsignedInteger(ORIGIN), new UnicodeString(origin));
        }

        Array array = new Array();
        for (byte[] signData : signDataList) {
            DataItem x = new ByteString(signData);
            array.add(x);
        }
        map.put(new UnsignedInteger(SIGN_DATA), array);
        DataItem path = derivationPath.toCbor();
        path.setTag(RegistryType.CRYPTO_KEYPATH.getTag());
        map.put(new UnsignedInteger(DERIVATION_PATH), path);
        return map;
    }

    public static NearSignRequest fromCbor(DataItem item) {
        byte[] requestId = null;
        List<byte[]> signDataList = null;
        CryptoKeypath derivationPath = null;
        String account = null;
        String origin = null;

        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGN_DATA) {
                Array signDataArray = (Array) map.get(uintKey);
                signDataList = new ArrayList<>(signDataArray.getDataItems().size());
                for (DataItem signData : signDataArray.getDataItems()) {
                    signDataList.add(((ByteString) signData).getBytes());
                }
            } else if (intKey == DERIVATION_PATH) {
                derivationPath = CryptoKeypath.fromCbor(map.get(uintKey));
            } else if (intKey == ACCOUNT) {
                account = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == ORIGIN) {
                origin = ((UnicodeString) map.get(uintKey)).getString();
            }
        }
        if (signDataList == null || derivationPath == null) {
            throw new IllegalStateException("required data field is missing");
        }
        return new NearSignRequest(signDataList, derivationPath, requestId, account, origin);
    }
}
