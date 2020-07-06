export interface ISymbol {
  fullName?: string;
  symbol?: string;
}

export class Aktien implements ISymbol {
  constructor(
    public fullName?: string,
    public symbol?: string
  ) {}
}
