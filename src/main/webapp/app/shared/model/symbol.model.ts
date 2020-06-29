export interface ISymbol {
  name?: string;
  symbol?: string;
}

export class Aktien implements ISymbol {
  constructor(
    public name?: string,
    public symbol?: string
  ) {}
}
