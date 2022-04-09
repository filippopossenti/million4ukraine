import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { MgrService } from './mgr.service';

declare var PayPal:any;

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

  targetDonation: number = 5;

  baseDonation: number = 5;

  waitMaskVisible: boolean = false;
  processingData: boolean = true;
  processDonate: boolean = false;

  constructor(private mgrService: MgrService) {
    this.thumbnailurl = mgrService.getThumbnailUrl();
  }

  ngOnInit() {
    this.loadLatestMessages();
  }

  preparePaypalDonateButton(uuid: any) {

    PayPal.Donation.Button({
        env: 'sandbox',
        hosted_button_id: '84N4EYYMYMNZQ',
        custom: uuid,
        image: {
            src: 'https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif',
            alt: 'Donate with PayPal button',
            title: 'PayPal - The safer, easier way to pay online!',
        },
        // notify_url: "https://422e-62-211-118-211.ngrok.io/",
        onComplete: (result: any) => {
            console.log(result);
            if (result.st === "Completed") {
                window.location.hash = "Upload?TX=" + result.tx + "&ID=" + result.cm;
            }
        }
    }).render('#donate-button');

  }

  onNameKeyUp(evt: any) {
    this.name = evt.target.value;
  }

  onEmailKeyUp(evt: any) {
    this.email = evt.target.value;
  }

  onNationalityChange(evt: any) {
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
      let img = document.createElement('img');
      img.onload = this.onImageLoaded.bind(this);
      img.src = tgt.result;
    }
  }

  onImageLoaded(evt: Event) {
    this.imageDataurl = this.resizeImage(evt.target, 720);
  }

  resizeImage(image: any, maxSize: number) {
    let width = image.width;
    let height = image.height;
    if (width > height) {
        if (width > maxSize) {
            height *= maxSize / width;
            width = maxSize;
        }
    } else {
        if (height > maxSize) {
            width *= maxSize / height;
            height = maxSize;
        }
    }
    let canvas = document.createElement('canvas');
    canvas.width = width;
    canvas.height = height;
    let ctx = canvas.getContext('2d')
    ctx!.drawImage(image, 0, 0, width, height);
    return canvas.toDataURL('image/jpeg');
  }

  onSizeChange(evt: any) {
    let values = evt.target.value.split("x");
    this.size_x = parseInt(values[0]);
    this.size_y = parseInt(values[1]);
    this.targetDonation = this.size_x * this.size_y * this.baseDonation;
  }


  upload() {
    this.waitMaskVisible = true;
    this.mgrService.submit({
      name: this.name,
      email: this.email,
      nationality: this.nationality,
      message: this.message,
      imageDataurl: this.imageDataurl,
      sizeX: this.size_x,
      sizeY: this.size_y
    }).subscribe((result: any) => {
      this.thumbnailurl = this.mgrService.getThumbnailUrl();
      this.processingData = false;
      this.processDonate = true;
      this.preparePaypalDonateButton(result["payload"]);
      this.loadLatestMessages();
    });
  }

  loadLatestMessages() {
    this.mgrService.getLatestDonations().subscribe(results => {
      this.latestDonations = results;
    });
  }
}
