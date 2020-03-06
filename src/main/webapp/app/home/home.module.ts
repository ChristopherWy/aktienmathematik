import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AktienmathematikSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { ChartsModule } from 'ng2-charts';

@NgModule({
  imports: [ChartsModule, AktienmathematikSharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent]
})
export class AktienmathematikHomeModule {}
