import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { MgrService } from './mgr.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Million 4 Ukraine';

  thumbnailurl: String = '';
  name: String = '';
  email: String = '';
  nationality: String = '';
  message: String = '';
  imageDataurl: String = '';
  size_x: number = 1;
  size_y: number = 1;

  latestDonations: any;

  constructor(private mgrService: MgrService) {
    this.thumbnailurl = mgrService.getThumbnailUrl();
  }

  ngOnInit() {
    this.loadLatestMessages();
  }

  onNameKeyUp(evt: any) {
    this.name = evt.target.value;
  }

  onEmailKeyUp(evt: any) {
    this.email = evt.target.value;
  }

  onNationalityKeyUp(evt: any) {
    this.nationality = evt.target.value;
  }

  onMessageKeyUp(evt: any) {
    this.message = evt.target.value;
  }

  onUploadedPictureChange(evt: any) {
    let f = evt.target.files[0];
    let reader = new FileReader();

    reader.onload = this.onFileUploaded.bind(this);
    reader.readAsDataURL(f);

  }

  onFileUploaded(evt: Event) {
    if(evt && evt.target) {
      let tgt: any = evt.target;
      this.imageDataurl = tgt.result;
    }
  }

  onSizeChange(evt: any) {
    let values = evt.target.value.split("x");
    this.size_x = parseInt(values[0]);
    this.size_y = parseInt(values[1]);
  }


  upload() {
    this.mgrService.submit({
      name: this.name,
      email: this.email,
      nationality: this.nationality,
      message: this.message,
      imageDataurl: this.imageDataurl,
      size_x: this.size_x,
      size_y: this.size_y
    }).subscribe(result => {
      this.thumbnailurl = this.mgrService.getThumbnailUrl();
      this.loadLatestMessages();
    });
  }

  loadLatestMessages() {
    this.mgrService.getLatestDonations().subscribe(results => {
      this.latestDonations = results;
    });
  }
}
