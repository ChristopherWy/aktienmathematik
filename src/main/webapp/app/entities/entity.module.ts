import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'aktien',
        loadChildren: () => import('./aktien/aktien.module').then(m => m.AktienmathematikAktienModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class AktienmathematikEntityModule {}
