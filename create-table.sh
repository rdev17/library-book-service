#!/bin/bash
### books table
echo "Creating [books] DynamoDB table in LocalStack..."

aws --endpoint-url=http://localhost:4566 dynamodb create-table \
    --table-name books \
    --region us-east-1 \
    --attribute-definitions AttributeName=title,AttributeType=S AttributeName=author,AttributeType=S \
    --key-schema AttributeName=title,KeyType=HASH AttributeName=author,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST

echo "DynamoDB table, [books], has been successfully created"

### checkouts table
echo "Creating [checkouts] DynamoDB table in LocalStack"

aws --endpoint-url=http://localhost:4566 dynamodb create-table \
    --table-name checkouts \
    --region us-east-1 \
    --attribute-definitions AttributeName=userId,AttributeType=S AttributeName=bookId,AttributeType=S \
    --key-schema AttributeName=userId,KeyType=HASH AttributeName=bookId,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST

echo "DynamoDB table, [checkouts], has been successfully created"

### Allow some time for the table to be initialized before setting TTL
sleep 10

aws --endpoint-url=http://localhost:4566 dynamodb update-time-to-live \
    --table-name checkouts \
    --time-to-live-specification '{"Enabled":true,"AttributeName":"expiresAt"}'

### members table
echo "Creating [members] DynamoDB table in LocalStack"

aws --endpoint-url=http://localhost:4566 dynamodb create-table \
    --table-name members \
    --region us-east-1 \
    --attribute-definitions AttributeName=userId,AttributeType=S \
    --key-schema AttributeName=userId,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST

echo "DynamoDB table, [members], has been successfully created"