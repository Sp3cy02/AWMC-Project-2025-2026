import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Inviti } from './inviti';

describe('Inviti', () => {
  let component: Inviti;
  let fixture: ComponentFixture<Inviti>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Inviti]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Inviti);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
