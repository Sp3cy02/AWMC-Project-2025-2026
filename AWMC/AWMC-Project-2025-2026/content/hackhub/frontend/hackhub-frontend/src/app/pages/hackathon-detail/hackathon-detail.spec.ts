import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HackathonDetail } from './hackathon-detail';

describe('HackathonDetail', () => {
  let component: HackathonDetail;
  let fixture: ComponentFixture<HackathonDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HackathonDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HackathonDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
