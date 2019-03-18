# slim-shady-detector
### Hackaton Face Recognition app

##### This project is based on:https://aws.amazon.com/blogs/machine-learning/build-your-own-face-recognition-service-using-amazon-rekognition/ 

### Prerequisitions: 
- AWS CLI and python 2.7.14 
- AWS account
- Angular 5.0.0 +
- Java 8.0.0 +

###### You can use cloudFormation from the blog article and just plug in frontend (Angular app and android app) or follow those manual steps: 

1. Confiure and check you IAM role: $aws sts get-caller-identity { "Account": "88888888888", "UserId": "AIDAI88YRPFQ26P2*****", "Arn": "arn:aws:iam::8888888888:user/ziuta.ziuta-ziuta@company.com" } ( role need to have following at least following policies applied: AmazonRekognitionFullAccess AmazonDynamoDBFullAccess AmazonS3FullAccess IAMFullAccess )

2.  Create collection in Rekognition using AWS CLI:
```
$aws rekognition create-collection --collection-id my_collection --region eu-west-1 console response: { "CollectionArn": "aws:rekognition:eu-west-1:848288068735:collection/my_collection", "FaceModelVersion": "3.0", }
```
3. Create a table in DYNAMO DB using AWS CLI: 
```
$aws dynamodb create-table --table-name my_collection --attribute-definitions AttributeName=RekognitionId,AttributeType=S --key-schema AttributeName=RekognitionId,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 --region eu-west-1 console response: { "TableDescription": { "TableArn": "arn:aws:dynamodb:eu-west-1:848288068735:table/my_collection", "AttributeDefinitions": [ { "AttributeName": "RekognitionId", "AttributeType": "S" } ], "ProvisionedThroughput": { "NumberOfDecreasesToday": 0, "WriteCapacityUnits": 1, "ReadCapacityUnits": 1 }, "TableSizeBytes": 0, "TableName": "my_collection", "TableStatus": "CREATING", "TableId": "9dbac75e-168f-400a-bbad-e1dc0734e7db", "KeySchema": [ { "KeyType": "HASH", "AttributeName": "RekognitionId" } ], "ItemCount": 0, "CreationDateTime": 1530049176.585 } }
```
4. Create Bucket in s3 using AWS CLI:
```
$ aws s3 mb s3://slimshady-bucket --region eu-west-1 console response: make_bucket: slimshady-bucket
```
5. apply the policies to create IAM Lambda Service LambdaRekognitionRole:
```
$aws iam create-role --role-name LambdaRekognitionRole --assume-role-policy-document file://trust-policy.json $aws iam put-role-policy --role-name LambdaRekognitionRole --policy-name LambdaPermissions --policy-document file://access-policy.json (make sure that the access-policy.json file has correct values in it for bucket and collection name)
```
6. Confiure lambda in console with s3 bucket upload trigger as per blog post (use a bucket configured in step 4 and LambdaRekognitionRole from step 5 and script from: Python\lambda\lambda_function) 
**make sure you have those dependencies: pip install boto3 pip install pillow* 
**make sure that the lambda refers to your collection*

7. Upload picture from images/MonthyPythonCollection to s3 bucket from AWS CLI: 
```
<YOUR-PATH>\slim-shady-hackathon (master -> origin) Î» python Python/S3upload/upload.py
```

**make sure that the update script refers to your bitbucket*

This step will: upload the pictures into bucket index/ folder and add metadata (Full name): check your bucket trigger lambda to create the keys into dynamodb table(step3) and add them to reckognition collection(step 2) (check lambda console and dynamoDB); 
test the service with test pictures specified in the queryResponse.py: <YOUR-PATH>slim-shady-hackathon (master -> origin) » python Python/RekognitionMatchQuery/queryResponse.py
