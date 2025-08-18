<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { color: #2c3e50; border-bottom: 1px solid #eee; padding-bottom: 10px; }
        .order-details { margin: 20px 0; }
        .product-table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        .product-table th { background: #f5f5f5; text-align: left; padding: 10px; }
        .product-table td { padding: 10px; border-bottom: 1px solid #eee; }
        .footer { margin-top: 30px; font-size: 0.8em; color: #7f8c8d; }
    </style>
</head>
<body>
    <div class="container">
        <h1 class="header">Order Confirmation: ${order.orderNumber}</h1>

        <p>Dear ${firstName} ${lastName},</p>
        <p>Thank you for your order! Here are your order details:</p>

        <div class="order-details">
            <h3>Order Summary</h3>
            <#-- Safe date handling for createdAt -->
            <p><strong>Order Date:</strong>
                <#if order.createdAt?is_date_like>
                    ${order.createdAt?string("MMMM d, yyyy")}
                <#elseif order.createdAt?contains("T")>
                    <#assign dateOnly = order.createdAt?substring(0, 10)>
                    <#assign parts = dateOnly?split("-")>
                    <#if parts?size == 3>
                        <#assign year = parts[0]>
                        <#assign month = parts[1]?number>
                        <#assign day = parts[2]?number>
                        <#assign monthNames = ["", "January", "February", "March", "April", "May", "June",
                                              "July", "August", "September", "October", "November", "December"]>
                        ${monthNames[month]} ${day}, ${year}
                    <#else>
                        ${order.createdAt}
                    </#if>
                <#else>
                    ${order.createdAt}
                </#if>
            </p>

            <#-- Safe date handling for estimatedDelivery -->
            <p><strong>Estimated Delivery:</strong>
                <#if order.estimatedDelivery?is_date_like>
                    ${order.estimatedDelivery?string("MMMM d, yyyy")}
                <#elseif order.estimatedDelivery?contains("-") && !order.estimatedDelivery?contains("T")>
                    <#assign parts = order.estimatedDelivery?split("-")>
                    <#if parts?size == 3>
                        <#assign year = parts[0]>
                        <#assign month = parts[1]?number>
                        <#assign day = parts[2]?number>
                        <#assign monthNames = ["", "January", "February", "March", "April", "May", "June",
                                              "July", "August", "September", "October", "November", "December"]>
                        ${monthNames[month]} ${day}, ${year}
                    <#else>
                        ${order.estimatedDelivery}
                    </#if>
                <#else>
                    ${order.estimatedDelivery}
                </#if>
            </p>

            <p><strong>Shipping Address:</strong></p>
            <address>
                ${address.addressLine},<br>
                ${address.city}, ${address.state},<br>
                ${address.zipCode}, ${address.country}
            </address>
        </div>

        <h3>Order Items</h3>
        <table class="product-table">
            <thead>
                <tr>
                    <th>Product</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>Total</th>
                </tr>
            </thead>
            <tbody>
                <#list order.items as item>
                <tr>
                    <td>${item.productName}</td>
                    <td>$${item.unitPrice?string("0.00")}</td>
                    <td>${item.quantity}</td>
                    <td>$${item.totalPrice?string("0.00")}</td>
                </tr>
                </#list>
            </tbody>
        </table>

        <div style="text-align: right;">
            <p><strong>Subtotal:</strong> $${order.subtotal?string("0.00")}</p>
            <p><strong>Tax:</strong> $${order.taxAmount?string("0.00")}</p>
            <p><strong>Shipping:</strong> $${order.shippingAmount?string("0.00")}</p>
            <p><strong>Discount:</strong> -$${order.discountAmount?string("0.00")}</p>
            <h3>Total: $${order.totalAmount?string("0.00")}</h3>
        </div>

        <div class="payment-info">
            <h3>Payment Information</h3>
            <p><strong>Payment Method:</strong> ${payment.paymentMethod}</p>
            <p><strong>Payment Status:</strong> ${payment.status}</p>
            <p><strong>Amount Paid:</strong> $${payment.amount?string("0.00")}</p>
        </div>

        <div class="footer">
            <p>If you have any questions, please contact our customer support.</p>
            <p>Thank you for shopping with us!</p>
        </div>
    </div>
</body>
</html>