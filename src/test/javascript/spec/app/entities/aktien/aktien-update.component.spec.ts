import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AktienmathematikTestModule } from '../../../test.module';
import { AktienUpdateComponent } from 'app/entities/aktien/aktien-update.component';
import { AktienService } from 'app/entities/aktien/aktien.service';
import { Aktien } from 'app/shared/model/aktien.model';

describe('Component Tests', () => {
  describe('Aktien Management Update Component', () => {
    let comp: AktienUpdateComponent;
    let fixture: ComponentFixture<AktienUpdateComponent>;
    let service: AktienService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AktienmathematikTestModule],
        declarations: [AktienUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(AktienUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AktienUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AktienService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Aktien(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Aktien();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
