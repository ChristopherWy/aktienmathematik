import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAktien } from 'app/shared/model/aktien.model';
import { AktienService } from './aktien.service';

@Component({
  templateUrl: './aktien-delete-dialog.component.html'
})
export class AktienDeleteDialogComponent {
  aktien?: IAktien;

  constructor(protected aktienService: AktienService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.aktienService.delete(id).subscribe(() => {
      this.eventManager.broadcast('aktienListModification');
      this.activeModal.close();
    });
  }
}
