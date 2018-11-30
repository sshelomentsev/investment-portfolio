import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentStructureComponent } from './current-structure.component';

describe('CurrentStructureComponent', () => {
  let component: CurrentStructureComponent;
  let fixture: ComponentFixture<CurrentStructureComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CurrentStructureComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CurrentStructureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
