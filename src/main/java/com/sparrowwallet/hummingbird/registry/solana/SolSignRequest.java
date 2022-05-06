package com.sparrowwallet.hummingbird.registry.solana;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;


public class SolSignRequest extends RegistryItem {
    public static final int REQUEST_ID = 1;
    public static final int SIGN_DATA = 2;
    public static final int DERIVATION_PATH = 3;
    public static final int ADDRESS = 4;
    public static final int ORIGIN = 5;
    public static final int TYPE = 6;

    private final byte[] requestId;
    private final byte[] signData;
    private final CryptoKeypath derivationPath;
    private final byte[] address;
    private final String origin;

    private final DataType type;

    public SolSignRequest(byte[] signData, CryptoKeypath derivationPath, Integer type) {
        this.requestId = null;
        this.signData = signData;
        this.derivationPath = derivationPath;
        this.address = null;
        this.origin = null;
        this.type = DataType.fromInteger(type);
    }


    public SolSignRequest(byte[] signData, CryptoKeypath derivationPath, byte[] requestId, Integer type) {
        this.requestId = requestId;
        this.signData = signData;
        this.derivationPath = derivationPath;
        this.address = null;
        this.origin = null;
        this.type = DataType.fromInteger(type);
    }


    public SolSignRequest(byte[] signData, CryptoKeypath derivationPath, byte[] requestId, byte[] address, String origin, Integer type) {
        this.requestId = requestId;
        this.signData = signData;
        this.derivationPath = derivationPath;
        this.address = address;
        this.origin = origin;
        this.type = DataType.fromInteger(type);
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getSignData() {
        return signData;
    }

    public String getDerivationPath() {
        return derivationPath.getPath();
    }

    public byte[] getMasterFingerprint() {
        return derivationPath.getSourceFingerprint();
    }

    public byte[] getAddress() {
        return address;
    }

    public String getOrigin() {
        return origin;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.SOL_SIGN_REQUEST;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }

        if (address != null) {
            map.put(new UnsignedInteger(ADDRESS), new ByteString(address));
        }

        if (origin != null) {
            map.put(new UnsignedInteger(ORIGIN), new UnicodeString(origin));
        }

        map.put(new UnsignedInteger(SIGN_DATA), new ByteString(signData));
        DataItem path = derivationPath.toCbor();
        path.setTag(RegistryType.CRYPTO_KEYPATH.getTag());
        map.put(new UnsignedInteger(DERIVATION_PATH), path);
        map.put(new UnsignedInteger(TYPE), new UnsignedInteger(type.getTypeIndex()));
        return map;
    }

    public static SolSignRequest fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] signData = null;
        CryptoKeypath derivationPath = null;
        byte[] address = null;
        String origin = null;
        Integer dataTypeIndex = null;

        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGN_DATA) {
                signData = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == DERIVATION_PATH) {
                derivationPath = CryptoKeypath.fromCbor(map.get(uintKey));
            } else if (intKey == ADDRESS) {
                address = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == ORIGIN) {
                origin = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == TYPE) {
                dataTypeIndex = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            }
        }

        if (signData == null || derivationPath == null) {
            throw new IllegalStateException("required data field is missing");
        }

        if (requestId != null && address != null) {
            return new SolSignRequest(signData, derivationPath, requestId, address, origin, dataTypeIndex);
        } else if (requestId != null) {
            return new SolSignRequest(signData, derivationPath, requestId, dataTypeIndex);
        } else if (address != null) {
            return new SolSignRequest(signData, derivationPath, address, dataTypeIndex);
        } else {
            return new SolSignRequest(signData, derivationPath, dataTypeIndex);
        }
    }

    public enum DataType {
        TRANSACTION("sign-type-transaction", 1),
        MESSAGE("sign-type-message", 2);

        private final String type;
        private final Integer typeIndex;

        DataType(String type, Integer typeIndex) {
            this.type = type;
            this.typeIndex = typeIndex;

        }

        public String getType() {
            return type;
        }

        public Integer getTypeIndex() {
            return typeIndex;
        }

        @Override
        public String toString() {
            return type;
        }

        public static DataType fromInteger(Integer typeIndex) {
            for (DataType dataType : DataType.values()) {
                if (dataType.getTypeIndex().equals(typeIndex)) {
                    return dataType;
                }
            }
            throw new IllegalArgumentException("Unknown sol data type: " + typeIndex);
        }
    }
}
