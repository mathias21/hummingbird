package com.sparrowwallet.hummingbird.registry;

import co.nstant.in.cbor.model.*;
import co.nstant.in.cbor.model.Number;

import java.math.BigInteger;

enum DataType {
    TRANSACTION("Transaction", 1),
    TYPED_DATA("TypedData", 2),
    PERSONAL_MESSAGE("PersonalMessage", 3),
    TYPED_TRANSACTION("TypedTransaction", 4);

    private final String type;
    private final Integer typeIndex;

    private DataType(String type, Integer typeIndex) {
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
        for(DataType dataType : values()) {
            if(dataType.getTypeIndex().equals(typeIndex)) {
                return dataType;
            }
        }

        throw new IllegalArgumentException("Unknown eth data type: " + typeIndex);
    }
}



public class EthSignRequest extends RegistryItem {
    public static final int REQUEST_ID = 1;
    public static final int SIGN_DATA = 2;
    public static final int DATA_TYPE = 3;
    public static final int CHAIN_ID = 4;
    public static final int DERIVATION_PATH = 5;
    public static final int ADDRESS = 6;

    private final byte[] requestId;
    private final byte[] signData;
    private final DataType dataType;
    private final Integer chainId;
    private final CryptoKeypath derivationPath;
    private final byte[] address;

    public EthSignRequest(byte[] signData, Integer dataType, Integer chainId, CryptoKeypath derivationPath ) {
        this.requestId = null;
        this.signData = signData;
        this.dataType = DataType.fromInteger(dataType);
        this.chainId = chainId;
        this.derivationPath = derivationPath;
        this.address = null;
    }


    public EthSignRequest(byte[] signData, Integer dataType, Integer chainId, CryptoKeypath derivationPath, byte[] requestId) {
        this.requestId = requestId;
        this.signData = signData;
        this.dataType = DataType.fromInteger(dataType);
        this.chainId = chainId;
        this.derivationPath = derivationPath;
        this.address = null;
    }


    public EthSignRequest(byte[] signData, Integer dataType, Integer chainId, CryptoKeypath derivationPath, byte[] requestId, byte[] address) {
        this.requestId = requestId;
        this.signData = signData;
        this.dataType = DataType.fromInteger(dataType);
        this.chainId = chainId;
        this.derivationPath = derivationPath;
        this.address = address;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getSignData() {
        return signData;
    }

    public String getDataType() {
        return dataType.toString();
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

    public int getChainId() {
        return chainId;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.ETH_SIGN_REQUEST;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if(requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }

        if(address != null) {
            map.put(new UnsignedInteger(ADDRESS), new ByteString(address));
        }

        map.put(new UnsignedInteger(SIGN_DATA), new ByteString(signData));
        map.put(new UnsignedInteger(DATA_TYPE), new UnsignedInteger(dataType.getTypeIndex()));
        map.put(new UnsignedInteger(CHAIN_ID), new UnsignedInteger(chainId));
        DataItem path = derivationPath.toCbor();
        path.setTag(RegistryType.CRYPTO_KEYPATH.getTag());
        map.put(new UnsignedInteger(DERIVATION_PATH), path);
        return map;
    }

    public static EthSignRequest fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] signData = null;
        Integer dataTypeIndex = null;
        Integer chainId = null;
        CryptoKeypath derivationPath = null;
        byte[] address = null;

        Map map = (Map)item;
        for(DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGN_DATA) {
                signData = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == DATA_TYPE) {
                dataTypeIndex = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            } else if (intKey == CHAIN_ID) {
                chainId = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            } else if (intKey == DERIVATION_PATH) {
                derivationPath = CryptoKeypath.fromCbor(map.get(uintKey));
            } else if (intKey == ADDRESS) {
                address = ((ByteString) map.get(uintKey)).getBytes();
            }
        }

        if(signData == null || dataTypeIndex == null || chainId == null | derivationPath == null) {
            throw new IllegalStateException("required data field is missing");
        }

        if(requestId != null && address != null) {
            return new EthSignRequest(signData, dataTypeIndex, chainId, derivationPath, requestId, address);
        } else if (requestId != null) {
            return new EthSignRequest(signData, dataTypeIndex, chainId, derivationPath, requestId);
        } else if (address != null) {
            return new EthSignRequest(signData, dataTypeIndex, chainId, derivationPath, address);
        } else {
            return new EthSignRequest(signData, dataTypeIndex, chainId, derivationPath);
        }
    }

}
