import { TestBed } from '@angular/core/testing';

import { MgrService } from './mgr.service';

describe('MgrService', () => {
  let service: MgrService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MgrService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
