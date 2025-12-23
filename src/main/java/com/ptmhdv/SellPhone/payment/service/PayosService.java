package com.ptmhdv.SellPhone.payment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.exception.PayOSException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.webhooks.WebhookData;

@Service
public class PayosService {

    private final PayOS payOS;

    @Value("${payos.return-url-base}")
    private String returnUrlBase;

    @Value("${payos.cancel-url-base}")
    private String cancelUrlBase;

    public PayosService(
            @Value("${PAYOS_CLIENT_ID}") String clientId,
            @Value("${PAYOS_API_KEY}") String apiKey,
            @Value("${PAYOS_CHECKSUM_KEY}") String checksumKey
    ) {
        this.payOS = new PayOS(clientId, apiKey, checksumKey);
    }

    private String withOrderId(String baseUrl, String orderId) {
        String sep = baseUrl.contains("?") ? "&" : "?";
        return baseUrl + sep + "orderId=" +
                java.net.URLEncoder.encode(orderId, java.nio.charset.StandardCharsets.UTF_8);
    }

    public CreatePaymentLinkResponse createPaymentLink(
            long orderCode,
            long amountVnd,
            String description,
            String appOrderId
    ) throws PayOSException {

        String returnUrl = withOrderId(returnUrlBase, appOrderId);
        String cancelUrl = withOrderId(cancelUrlBase, appOrderId);

        CreatePaymentLinkRequest req = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amountVnd)
                .description(description)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .build();

        return payOS.paymentRequests().create(req);
    }

    // ✅ nhận webhook dạng Object để khỏi phụ thuộc vn.payos.type.Webhook
    public WebhookData verifyWebhook(Object body) throws Exception {
        return payOS.webhooks().verify(body);
    }
}
