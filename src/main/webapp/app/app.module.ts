import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { AktienmathematikSharedModule } from 'app/shared/shared.module';
import { AktienmathematikCoreModule } from 'app/core/core.module';
import { AktienmathematikAppRoutingModule } from './app-routing.module';
import { AktienmathematikHomeModule } from './home/home.module';
import { AktienmathematikEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';
import {AutocompleteLibModule} from 'angular-ng-autocomplete';

@NgModule({
  imports: [
    BrowserModule,
    AktienmathematikSharedModule,
    AktienmathematikCoreModule,
    AktienmathematikHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    AktienmathematikEntityModule,
    AktienmathematikAppRoutingModule,
    AutocompleteLibModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent]
})
export class AktienmathematikAppModule {}
