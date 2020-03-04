import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IAktien, Aktien } from 'app/shared/model/aktien.model';
import { AktienService } from './aktien.service';

@Component({
  selector: 'jhi-aktien-update',
  templateUrl: './aktien-update.component.html'
})
export class AktienUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    symbol: [],
    date: [],
    open: [],
    close: [],
    high: [],
    low: [],
    volume: []
  });

  constructor(protected aktienService: AktienService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ aktien }) => {
      if (!aktien.id) {
        const today = moment().startOf('day');
        aktien.date = today;
      }

      this.updateForm(aktien);
    });
  }

  updateForm(aktien: IAktien): void {
    this.editForm.patchValue({
      id: aktien.id,
      symbol: aktien.symbol,
      date: aktien.date ? aktien.date.format(DATE_TIME_FORMAT) : null,
      open: aktien.open,
      close: aktien.close,
      high: aktien.high,
      low: aktien.low,
      volume: aktien.volume
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const aktien = this.createFromForm();
    if (aktien.id !== undefined) {
      this.subscribeToSaveResponse(this.aktienService.update(aktien));
    } else {
      this.subscribeToSaveResponse(this.aktienService.create(aktien));
    }
  }

  private createFromForm(): IAktien {
    return {
      ...new Aktien(),
      id: this.editForm.get(['id'])!.value,
      symbol: this.editForm.get(['symbol'])!.value,
      date: this.editForm.get(['date'])!.value ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      open: this.editForm.get(['open'])!.value,
      close: this.editForm.get(['close'])!.value,
      high: this.editForm.get(['high'])!.value,
      low: this.editForm.get(['low'])!.value,
      volume: this.editForm.get(['volume'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAktien>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
