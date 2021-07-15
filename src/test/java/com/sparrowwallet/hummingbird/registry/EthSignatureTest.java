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

import java.util.Arrays;
import java.util.List;

public class EthSignatureTest {
    @Test
    public void testEthSignature() throws CborException {
        String signature = "d4f0a7bcd95bba1fbb1051885054730e3f47064288575aacc102fbbf6a9a14daa066991e360d3e3406c20c00a40973eff37c7d641e5b351ec4a99bfe86f335f713";
        byte[] signData = TestUtils.hexToBytes(signature);

        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        byte[] uuidBytes = bb.array();



        EthSignature ethSignature = new EthSignature(signData, uuidBytes);

        List<DataItem> items = CborDecoder.decode(TestUtils.hexToBytes(TestUtils.encode(ethSignature.toCbor())));
        EthSignature originResponse = EthSignature.fromCbor(items.get(0));

        byte[] uuidByte = originResponse.getRequestId();

        ByteBuffer byteBuffer = ByteBuffer.wrap(uuidByte);
        Long high = byteBuffer.getLong();
        Long low = byteBuffer.getLong();
        UUID uuid1 = new UUID(high, low);

        Assert.assertEquals("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d", uuid1.toString());


        String ur = "ur:eth-signature/oeadtpdagdndcawmgtfrkigrpmndutdnbtkgfssbjnaohdfptywtosrftahprdctrkbegylogdghjkbafhflamfwlohghtpsseaozorsimnybbtnnbiynlckenbtfmeeamsabnaeoxasjkwswfkekiieckhpecckssptndzelnwfecylbwdlsgvazt";
        Assert.assertEquals(ur, originResponse.toUR().toString());
    }
}