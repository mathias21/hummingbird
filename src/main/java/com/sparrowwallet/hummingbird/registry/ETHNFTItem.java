package com.sparrowwallet.hummingbird.registry;

import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class ETHNFTItem extends RegistryItem {
    public static final int CHAIN_ID = 1;
    public static final int CONTRACT_ADDRESS = 2;
    public static final int NAME = 3;
    public static final int MEDIA_DATA = 4;

    private final int chainId;
    private final String name;
    private final String contractAddress;
    private final String mediaData;

    public ETHNFTItem(int chainId, String name, String contractAddress, String mediaData) {
        this.chainId = chainId;
        this.name = name;
        this.contractAddress = contractAddress;
        this.mediaData = mediaData;
    }

    public int getChainId() {
        return chainId;
    }

    public String getName() {
        return name;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String getMediaData() {
        return mediaData;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        map.put(new UnsignedInteger(CHAIN_ID), new UnsignedInteger(chainId));
        map.put(new UnsignedInteger(CONTRACT_ADDRESS), new UnicodeString(contractAddress));
        map.put(new UnsignedInteger(NAME), new UnicodeString(name));
        map.put(new UnsignedInteger(MEDIA_DATA), new UnicodeString(mediaData));
        return map;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.ETH_NFT_ITEM;
    }

    public static ETHNFTItem fromCbor(DataItem item) {
        Integer chainId = null;
        String name = null;
        String contractAddress = null;
        String mediaData = null;

        Map map = (Map)item;
        for(DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == CHAIN_ID) {
                chainId = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            } else if (intKey == CONTRACT_ADDRESS) {
                contractAddress = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == NAME) {
                name = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == MEDIA_DATA) {
                mediaData = ((UnicodeString) map.get(uintKey)).getString();
            }
        }
        if(chainId == null || name == null || contractAddress == null || mediaData == null) {
            throw new IllegalStateException("required data field is missing");
        }
        return new ETHNFTItem(chainId, name, contractAddress, mediaData);
    }
}
