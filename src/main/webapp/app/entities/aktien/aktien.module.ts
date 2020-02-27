import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AktienmathematikSharedModule } from 'app/shared/shared.module';
import { AktienComponent } from './aktien.component';
import { AktienDetailComponent } from './aktien-detail.component';
import { AktienUpdateComponent } from './aktien-update.component';
import { AktienDeleteDialogComponent } from './aktien-delete-dialog.component';
import { aktienRoute } from './aktien.route';

@NgModule({
  imports: [AktienmathematikSharedModule, RouterModule.forChild(aktienRoute)],
  declarations: [AktienComponent, AktienDetailComponent, AktienUpdateComponent, AktienDeleteDialogComponent],
  entryComponents: [AktienDeleteDialogComponent]
})
export class AktienmathematikAktienModule {}
