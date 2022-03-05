import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MgrService } from './mgr.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Million 4 Ukraine';

  thumbnailurl: String = '';
  name: String ='';
  nationality: String = '';
  message: String = '';
  imageDataurl: String = '';

  constructor(private mgrService: MgrService) {
    this.thumbnailurl = mgrService.getThumbnailUrl();
  }

  onNameKeyUp(evt: any) {
    this.name = evt.target.value;
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


  upload() {
    this.mgrService.submit({
      name: this.name,
      nationality: this.nationality,
      message: this.message,
      imageDataurl: this.imageDataurl
    }).subscribe(result => {
      this.thumbnailurl = this.mgrService.getThumbnailUrl();
    });
  }
}
