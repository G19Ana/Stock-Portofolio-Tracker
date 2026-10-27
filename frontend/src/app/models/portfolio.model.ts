export interface HoldingView {
  id: number;
  symbol: string;
  shares: number;
  buyPrice: number;
  currentPrice: number;
  totalCost: number;
  marketValue: number;
  pnl: number;
  pnlPct: number;
  allocationPct: number;
}

export interface PortfolioView {
  totalCost: number;
  totalValue: number;
  totalPnl: number;
  totalPnlPct: number;
  holdings: HoldingView[];
}

export interface HistoryPoint {
  date: string;
  close: number;
}

export interface HoldingReq {
  symbol: string;
  shares: number;
  buyPrice: number;
}

export interface AlertReq {
  symbol: string;
  targetPrice: number;
  direction: 'ABOVE' | 'BELOW';
  email: string;
}

export interface AlertItem {
  id: number;
  symbol: string;
  targetPrice: number;
  direction: 'ABOVE' | 'BELOW';
  email: string;
  active: boolean;
}