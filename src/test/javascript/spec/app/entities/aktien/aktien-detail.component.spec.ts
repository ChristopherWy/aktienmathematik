import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AktienmathematikTestModule } from '../../../test.module';
import { AktienDetailComponent } from 'app/entities/aktien/aktien-detail.component';
import { Aktien } from 'app/shared/model/aktien.model';

describe('Component Tests', () => {
  describe('Aktien Management Detail Component', () => {
    let comp: AktienDetailComponent;
    let fixture: ComponentFixture<AktienDetailComponent>;
    const route = ({ data: of({ aktien: new Aktien(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AktienmathematikTestModule],
        declarations: [AktienDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(AktienDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AktienDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load aktien on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.aktien).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
