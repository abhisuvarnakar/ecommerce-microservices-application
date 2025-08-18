<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; }
        .container { max-width: 600px; margin: 20px auto; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Welcome to ${appName}, ${username}!</h2>
        <p>Your registration is now complete.</p>

        <#if loginUrl??>
        <p>Get started: <a href="${loginUrl}">Login here</a></p>
        </#if>

        <p>Need help? Contact <a href="mailto:${supportEmail}">support</a>.</p>
    </div>
</body>
</html>