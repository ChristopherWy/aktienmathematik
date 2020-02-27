import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IAktien, Aktien } from 'app/shared/model/aktien.model';
import { AktienService } from './aktien.service';
import { AktienComponent } from './aktien.component';
import { AktienDetailComponent } from './aktien-detail.component';
import { AktienUpdateComponent } from './aktien-update.component';

@Injectable({ providedIn: 'root' })
export class AktienResolve implements Resolve<IAktien> {
  constructor(private service: AktienService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAktien> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((aktien: HttpResponse<Aktien>) => {
          if (aktien.body) {
            return of(aktien.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Aktien());
  }
}

export const aktienRoute: Routes = [
  {
    path: '',
    component: AktienComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'Aktiens'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: AktienDetailComponent,
    resolve: {
      aktien: AktienResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Aktiens'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: AktienUpdateComponent,
    resolve: {
      aktien: AktienResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Aktiens'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: AktienUpdateComponent,
    resolve: {
      aktien: AktienResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Aktiens'
    },
    canActivate: [UserRouteAccessService]
  }
];
