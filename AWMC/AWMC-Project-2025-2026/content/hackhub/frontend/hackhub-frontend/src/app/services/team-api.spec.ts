import { TestBed } from '@angular/core/testing';

import { TeamApi } from './team-api';

describe('TeamApi', () => {
  let service: TeamApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
