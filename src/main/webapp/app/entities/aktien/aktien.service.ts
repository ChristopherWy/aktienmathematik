import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IAktien } from 'app/shared/model/aktien.model';
import { ISymbol } from 'app/shared/model/symbol.model';

type EntityResponseType = HttpResponse<IAktien>;
type EntityArrayResponseType = HttpResponse<IAktien[]>;

@Injectable({ providedIn: 'root' })
export class AktienService {
  public resourceUrl = SERVER_API_URL + 'api/aktiens';

  public symbolsUrl = SERVER_API_URL + 'api/symbols';

  constructor(protected http: HttpClient) {}

  create(aktien: IAktien): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(aktien);
    return this.http
      .post<IAktien>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(aktien: IAktien): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(aktien);
    return this.http
      .put<IAktien>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IAktien>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  querySymbols(): Observable<EntityArrayResponseType> {
    return this.http
      .get<ISymbol[]>(this.symbolsUrl, { observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAktien[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(aktien: IAktien): IAktien {
    const copy: IAktien = Object.assign({}, aktien, {
      date: aktien.date && aktien.date.isValid() ? aktien.date.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? moment(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((aktien: IAktien) => {
        aktien.date = aktien.date ? moment(aktien.date) : undefined;
      });
    }
    return res;
  }
}
