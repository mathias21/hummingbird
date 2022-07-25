package com.sparrowwallet.hummingbird.registry;

import com.sparrowwallet.hummingbird.TestUtils;
import com.sparrowwallet.hummingbird.registry.near.NearSignRequest;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NearSignRequestTest {

    @Test
    public void testNearSignRequest() {


        String signHex = "8e53e7b10656816de70824e3016fc1a277e77825e12825dc4f239f418ab2e04e";
        byte[] signData = TestUtils.hexToBytes(signHex);
        List<byte[]> signDataList = new ArrayList<>();
        signDataList.add(signData);
        signDataList.add(signData);


        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        byte[] uuidBytes = bb.array();


        List<PathComponent> components = new ArrayList<>();
        components.add(new PathComponent(44, true));
        components.add(new PathComponent(397, true));
        components.add(new PathComponent(0, true));
        components.add(new PathComponent(0, true));
        components.add(new PathComponent(1, true));

        String masterFinger = "78230804";
        CryptoKeypath keyPath = new CryptoKeypath(components, TestUtils.hexToBytes(masterFinger), null);
        NearSignRequest nearSignRequest = new NearSignRequest(signDataList, keyPath, uuidBytes, null, "nearwallet");


        String urStr = "ur:near-sign-request/oxadtpdagdndcawmgtfrkigrpmndutdnbtkgfssbjnaolfhdcxmnguvdpaamhflyjnvdaydkvladjlseoektvdksdavydedauogwcnnefpleprvtglhdcxmnguvdpaamhflyjnvdaydkvladjlseoektvdksdavydedauogwcnnefpleprvtglaxtaaddyoeadlecsdwykcfadlgykaeykaeykadykaocykscnayaaahimjtihhsjpkthsjzjzihjyrdahdssk";
        Assert.assertEquals(urStr, nearSignRequest.toUR().toString());
    }
}
