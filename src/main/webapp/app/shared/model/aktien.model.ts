import { Moment } from 'moment';

export interface IAktien {
  id?: number;
  symbol?: string;
  date?: Moment;
  open?: number;
  close?: number;
  high?: number;
  low?: number;
  volume?: number;
}

export class Aktien implements IAktien {
  constructor(
    public id?: number,
    public symbol?: string,
    public date?: Moment,
    public open?: number,
    public close?: number,
    public high?: number,
    public low?: number,
    public volume?: number
  ) {}
}
