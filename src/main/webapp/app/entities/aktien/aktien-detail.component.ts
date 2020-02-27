import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAktien } from 'app/shared/model/aktien.model';

@Component({
  selector: 'jhi-aktien-detail',
  templateUrl: './aktien-detail.component.html'
})
export class AktienDetailComponent implements OnInit {
  aktien: IAktien | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ aktien }) => (this.aktien = aktien));
  }

  previousState(): void {
    window.history.back();
  }
}
