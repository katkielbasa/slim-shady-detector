import boto3

s3 = boto3.resource('s3')

# Get list of objects for indexing
images=[
       ('images/MonthyPythonCollection/Trish2.JPG','Patricia Hart', 'ph@company.com'),
       ('images/MonthyPythonCollection/Trish3.JPG','Patricia Hart', 'ph@company.com'),
       ('images/MonthyPythonCollection/Kasia1.JPG','Katarzyna Kielbasa-Katynska', 'kasia@company.com'),
       ('images/MonthyPythonCollection/Kasia2.JPG','Katarzyna Kielbasa-Katynska', 'kasia@company.com'),
       ('images/MonthyPythonCollection/Hussain1.jpg','Hussain Ar', 'husain@company.com'),
       ('images/MonthyPythonCollection/Hussain2.JPG','Hussain Ar', 'husain@company.com'),
       ('images/MonthyPythonCollection/Sean1.JPG','Sean xxxx', 'sean@company.com'),
       ('images/MonthyPythonCollection/Sean2.JPG','Sean xxxx', 'sean@company.com')
      ]

# Iterate through list to upload objects to S3   
for image in images:
    file = open(image[0],'rb')
    object = s3.Object('slimshady-bucket','index/'+ image[0])
    ret = object.put(Body=file,
                    Metadata={'FullName':image[1]}
                    )
