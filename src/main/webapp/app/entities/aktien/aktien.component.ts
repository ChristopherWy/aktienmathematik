import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAktien } from 'app/shared/model/aktien.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { AktienService } from './aktien.service';
import { AktienDeleteDialogComponent } from './aktien-delete-dialog.component';
import { AktienUpdateComponent } from './aktien-update.component';
import { FormBuilder } from '@angular/forms';
import {ISymbol} from "app/shared/model/symbol.model";

@Component({
  selector: 'jhi-aktien',
  templateUrl: './aktien.component.html'
})
export class AktienComponent implements OnInit, OnDestroy {
  aktiens?: IAktien[];
  symbols?: ISymbol[];
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  update: AktienUpdateComponent;
  keyword = 'fullName';

  constructor(
    protected aktienService: AktienService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    private fb: FormBuilder
  ) {
    this.update = new AktienUpdateComponent(aktienService, activatedRoute, fb);

    this.aktienService.querySymbols().subscribe(
      (x: HttpResponse<ISymbol[]>) => this.setSymbols(x.body),
      () => this.onError()
    )
  }

  selectEvent(item: any): void {
    // do something with selected item
  }

  onChangeSearch(search: string): void {
    // fetch remote data from here
    // And reassign the 'data' which is binded to 'data' property.
  }

  onFocused(e: any): void {
    // do something
  }

  loadPage(page?: number): void {
    const pageToLoad: number = page || this.page;

    this.aktienService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe(
        (res: HttpResponse<IAktien[]>) => this.onSuccess(res.body, res.headers, pageToLoad),
        () => this.onError()
      );
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(data => {
      this.page = data.pagingParams.page;
      this.ascending = data.pagingParams.ascending;
      this.predicate = data.pagingParams.predicate;
      this.ngbPaginationPage = data.pagingParams.page;
      this.loadPage();
    });
    this.registerChangeInAktiens();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IAktien): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInAktiens(): void {
    this.eventSubscriber = this.eventManager.subscribe('aktienListModification', () => this.loadPage());
  }

  delete(aktien: IAktien): void {
    const modalRef = this.modalService.open(AktienDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.aktien = aktien;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected setSymbols(data: ISymbol[] | null): void {
    this.symbols = data || [];
    //console.log(data);
    //this.aktiens = data || [];
  }

  protected onSuccess(data: IAktien[] | null, headers: HttpHeaders, page: number): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.router.navigate(['/aktien'], {
      queryParams: {
        page: this.page,
        size: this.itemsPerPage,
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc')
      }
    });
    this.aktiens = data || [];
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page;
  }
}
