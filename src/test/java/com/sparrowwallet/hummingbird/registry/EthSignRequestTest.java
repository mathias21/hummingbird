package com.sparrowwallet.hummingbird.registry;

import java.util.ArrayList;
import java.util.UUID;
import java.nio.ByteBuffer;
import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import com.sparrowwallet.hummingbird.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class EthSignRequestTest {
    @Test
    public void testEthSingRequest() throws CborException {
        String rlpDataHex = "f849808609184e72a00082271094000000000000000000000000000000000000000080a47f7465737432000000000000000000000000000000000000000000000000000000600057808080";
        byte[] signData = TestUtils.hexToBytes(rlpDataHex);

        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        byte[] uuidBytes = bb.array();

        List<PathComponent> components = new ArrayList<>();
        components.add(new PathComponent(44, true));
        components.add(new PathComponent(1, true));
        components.add(new PathComponent(1, true));
        components.add(new PathComponent(0, false));
        components.add(new PathComponent(1, false));
        CryptoKeypath keyPath = new CryptoKeypath(components, null, null);

        EthSignRequest ethSignRequest = new EthSignRequest(signData, EthSignRequest.DataType.TRANSACTION.getTypeIndex(), 1, keyPath ,uuidBytes);

        List<DataItem> items = CborDecoder.decode(TestUtils.hexToBytes(TestUtils.encode(ethSignRequest.toCbor())));
        EthSignRequest originRequest = EthSignRequest.fromCbor(items.get(0));

        Assert.assertEquals(1, originRequest.getChainId());
        Assert.assertEquals("Transaction", originRequest.getDataType());
        Assert.assertEquals(rlpDataHex, TestUtils.bytesToHex(originRequest.getSignData()));

        byte[] uuidByte = originRequest.getRequestId();

        ByteBuffer byteBuffer = ByteBuffer.wrap(uuidByte);
        Long high = byteBuffer.getLong();
        Long low = byteBuffer.getLong();
        UUID uuid1 = new UUID(high, low);

        Assert.assertEquals("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d", uuid1.toString());

        Assert.assertEquals("44'/1'/1'/0/1", originRequest.getDerivationPath());

        String ur = "ur:eth-sign-request/onadtpdagdndcawmgtfrkigrpmndutdnbtkgfssbjnaohdgryagalalnascsgljpnbaelfdibemwaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaelaoxlbjyihjkjyeyaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaehnaehglalalaaxadaaadahtaaddyoyadlecsdwykadykadykaewkadwkknztwfje";
        Assert.assertEquals(ur, ethSignRequest.toUR().toString());
    }
}