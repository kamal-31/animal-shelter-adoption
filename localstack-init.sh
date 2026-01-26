#!/bin/bash

echo "Creating S3 bucket for animal shelter images..."
awslocal s3 mb s3://animal-shelter-images
awslocal s3api put-bucket-cors --bucket animal-shelter-images --cors-configuration '{
  "CORSRules": [
    {
      "AllowedOrigins": ["http://localhost:5173"],
      "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
      "AllowedHeaders": ["*"],
      "MaxAgeSeconds": 3000
    }
  ]
}'
echo "S3 bucket created successfully!"