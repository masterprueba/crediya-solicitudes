# AWS Credentials Configuration Guide

## Method 1: Environment Variables (Recommended for Development)

Set these environment variables in your system:

### Windows (Command Prompt)
```cmd
set AWS_ACCESS_KEY_ID=your-access-key-id
set AWS_SECRET_ACCESS_KEY=your-secret-access-key
set AWS_DEFAULT_REGION=us-east-2
```

### Windows (PowerShell)
```powershell
$env:AWS_ACCESS_KEY_ID="your-access-key-id"
$env:AWS_SECRET_ACCESS_KEY="your-secret-access-key"
$env:AWS_DEFAULT_REGION="us-east-2"
```

### Linux/Mac
```bash
export AWS_ACCESS_KEY_ID=your-access-key-id
export AWS_SECRET_ACCESS_KEY=your-secret-access-key
export AWS_DEFAULT_REGION=us-east-2
```

## Method 2: AWS Credentials File

Create a credentials file at:
- Windows: `C:\Users\%USERNAME%\.aws\credentials`
- Linux/Mac: `~/.aws/credentials`

Content:
```ini
[default]
aws_access_key_id = your-access-key-id
aws_secret_access_key = your-secret-access-key
region = us-east-2
```

## Method 3: Application Properties (For Testing Only - Not Recommended for Production)

Add to your application.yaml:
```yaml
adapter:
  sqs:
    region: "us-east-2"
    queueUrl: "https://sqs.us-east-2.amazonaws.com/294568841222/solicitud-aprobada-rechazada"
    accessKey: "your-access-key-id"
    secretKey: "your-secret-access-key"
```

And modify SQSSenderConfig.java to use these properties.

## Method 4: For Local Development with LocalStack

If you're using LocalStack for local AWS services:

```yaml
adapter:
  sqs:
    region: "us-east-2"
    queueUrl: "http://localhost:4566/000000000000/solicitud-aprobada-rechazada"
    endpoint: "http://localhost:4566"
```

And set environment variables:
```
AWS_ACCESS_KEY_ID=test
AWS_SECRET_ACCESS_KEY=test
```

## Important Security Notes:

1. **Never commit AWS credentials to version control**
2. **Use environment variables or AWS IAM roles in production**
3. **For production, use AWS IAM roles attached to your EC2 instances or containers**
4. **Consider using AWS Systems Manager Parameter Store or AWS Secrets Manager for production**

## Getting Your AWS Credentials:

1. Log in to AWS Console
2. Go to IAM (Identity and Access Management)
3. Create a new user or use existing user
4. Attach policy: `AmazonSQSFullAccess` (or create custom policy with minimal permissions)
5. Create access keys in the "Security credentials" tab
6. Use the generated Access Key ID and Secret Access Key
