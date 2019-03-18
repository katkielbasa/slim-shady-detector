import { NgModule,Component,Pipe,OnInit} from '@angular/core';
import {FormGroup,FormControl,Validators,FormBuilder} from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import * as AWS from 'aws-sdk';
import * as S3 from 'aws-sdk/clients/s3';
import { Constants } from '../constants';
@Component({
  selector: 'app-upload-form',
  templateUrl: './upload-form.component.html',
  styleUrls: ['./upload-form.component.css']
})
export class UploadFormComponent implements OnInit {

  myform: FormGroup;
  firstName: FormControl;
  lastName: FormControl;
  fullName: string;
  email: FormControl;
  password: FormControl;
  selectedFile: File;

  ngOnInit() {
    this.createFormControls();
    this.createForm();
  }

  createFormControls() {
    this.firstName = new FormControl('', Validators.required);
    this.lastName = new FormControl('', Validators.required);
    this.email = new FormControl('', [
      Validators.required,
      Validators.pattern("[^@]+@company\.com$")
    ]);
    this.password = new FormControl('', [
      Validators.required
        ]);
  }

  createForm() {
    this.myform = new FormGroup({
      name: new FormGroup({
        firstName: this.firstName,
        lastName: this.lastName,
      }),
      email: this.email,
      password: this.password,
    });
  }

  // upload() {
  //   this.fullName = this.firstName.value + this.lastName.value;
  //   const AWSService = AWS;
  //   const region = Constants.AWS_REGION;
  //   const bucketName = Constants.BUCKET_NAME;
  //   const IdentityPoolId = Constants.IDENTITY_POOL;
  //   //Configures the AWS service and initial authorization
  //   AWSService.config.update({
  //     region: region,
  //     credentials: new AWSService.CognitoIdentityCredentials({
  //       IdentityPoolId: Constants.IDENTITY_POOL
  //     })
  //   });
  // //adds the S3 service, make sure the api version and bucket are correct
  //   const s3 = new AWSService.S3({
  //     apiVersion: Constants.AWS_API_VERSION,
  //     params: { Bucket: bucketName}
  //   });
  // //I store this in a variable for retrieval later
  //   //this.image = file.name;
    
  //   s3.upload({ Key: 'index/'+ this.fullName+'/'+this.selectedFile.name, Bucket: bucketName, Body: this.selectedFile, ACL: 'public-read', Metadata: {"fullname" : this.fullName} }, function (err, data) {
  //    if (err) {
  //      console.log(err, 'there was an error uploading your file');
  //    }
  //  });
  // }
  uploadfile(file) {
    this.fullName = this.firstName.value + ' '+ this.lastName.value;
    const userFolder = this.firstName.value + this.lastName.value

    const bucket = new S3(
      {
        accessKeyId: Constants.AWS_ACCESS_KEY_ID,
        secretAccessKey: Constants.AWS_SECRET_ACCESS_KEY,
        region: Constants.AWS_REGION
      }
    );
 
    const params = {
      Bucket: Constants.BUCKET_NAME,
      Key: 'index/'+ this.fullName + '/' + file.name,
      Body: file,
      Metadata: {'fullname' : this.fullName} ,
      ACL: 'public-read'
    };
 
    bucket.upload(params, function (err, data) {
      if (err) {
  //    console.log('There was an error uploading your file: ', err);
        return false;
      }
  //    console.log('Successfully uploaded file.', data);
      return true;
    });
  }
 
   fileEvent(fileInput:any) {
     this.selectedFile = fileInput.target.files[0];
   }

    onSubmit() {
    if (this.myform.valid) {
  //    console.log("Form Submitted!");
 //     console.log('selected file submit: ',this.selectedFile);
      this.uploadfile(this.selectedFile);
      this.myform.reset();
    } 
  }
}
