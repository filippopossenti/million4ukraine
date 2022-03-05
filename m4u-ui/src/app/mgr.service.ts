import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class MgrService {

  constructor(private http: HttpClient) { }

  getBaseUrl() {
    if(location.port=='4200') {
      return location.protocol + "//" + location.hostname + ":8080";
    }
    else {
      return location.protocol + "//" + location.host;
    }
  }

  getThumbnailUrl() {
    return this.getBaseUrl() + "/thumbnail?ts=" + (new Date()).getTime();
  }

  submit(data: any) {
    let baseurl = this.getBaseUrl();

    return this.http.post(`${baseurl}/submit`, data);
  }
}
