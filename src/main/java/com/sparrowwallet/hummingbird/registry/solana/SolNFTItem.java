package com.sparrowwallet.hummingbird.registry.solana;

import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class SolNFTItem extends RegistryItem {

    public static final int MINT_ADDRESS = 1;
    public static final int COLLECTION_NAME = 2;
    public static final int NAME = 3;
    public static final int MEDIA_DATA = 4;

    private final String mintAddress;
    private final String collectionName;
    private final String name;
    private final String mediaData;


    public SolNFTItem(String mintAddress, String collectionName, String name, String mediaData) {
        this.mintAddress = mintAddress;
        this.collectionName = collectionName;
        this.name = name;
        this.mediaData = mediaData;
    }


    public String getMintAddress() {
        return mintAddress;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getName() {
        return name;
    }

    public String getMediaData() {
        return mediaData;
    }



    @Override
    public DataItem toCbor() {
        Map map = new Map();
        map.put(new UnsignedInteger(MINT_ADDRESS), new UnicodeString(mintAddress));
        map.put(new UnsignedInteger(COLLECTION_NAME), new UnicodeString(collectionName));
        map.put(new UnsignedInteger(NAME), new UnicodeString(name));
        map.put(new UnsignedInteger(MEDIA_DATA), new UnicodeString(mediaData));
        return map;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.SOL_NFT_ITEM;
    }

    public static SolNFTItem fromCbor(DataItem item) {
        String mintAddress = null;
        String collectionName = null;
        String name = null;
        String mediaData = null;

        Map map = (Map)item;
        for(DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == MINT_ADDRESS) {
                mintAddress = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == COLLECTION_NAME) {
                collectionName = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == NAME) {
                name = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == MEDIA_DATA) {
                mediaData = ((UnicodeString) map.get(uintKey)).getString();
            }
        }
        if(mintAddress == null || collectionName == null || name == null || mediaData == null) {
            throw new IllegalStateException("required data field is missing");
        }
        return new SolNFTItem(mintAddress, collectionName, name, mediaData);
    }
}
