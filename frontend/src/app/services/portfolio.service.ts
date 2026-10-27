import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AlertItem, AlertReq, HistoryPoint, HoldingReq, PortfolioView } from '../models/portfolio.model';

@Injectable({ providedIn: 'root' })
export class PortfolioService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api';

  getPortfolio(): Observable<PortfolioView> {
    return this.http.get<PortfolioView>(`${this.base}/portfolio`);
  }

  addHolding(req: HoldingReq): Observable<void> {
    return this.http.post<void>(`${this.base}/portfolio/holdings`, req);
  }

  deleteHolding(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/portfolio/holdings/${id}`);
  }

  getHistory(symbol: string): Observable<HistoryPoint[]> {
    return this.http.get<HistoryPoint[]>(`${this.base}/portfolio/history/${symbol}`);
  }

  getAlerts(): Observable<AlertItem[]> {
    return this.http.get<AlertItem[]>(`${this.base}/alerts`);
  }

  addAlert(req: AlertReq): Observable<void> {
    return this.http.post<void>(`${this.base}/alerts`, req);
  }

  deleteAlert(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/alerts/${id}`);
  }
}