import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TrendLineComponent } from './trend-line.component';

describe('TrendLineComponent', () => {
  let component: TrendLineComponent;
  let fixture: ComponentFixture<TrendLineComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TrendLineComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TrendLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
