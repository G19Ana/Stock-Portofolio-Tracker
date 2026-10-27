import { Component, ElementRef, OnInit, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, registerables } from 'chart.js';
import { PortfolioService } from '../../services/portfolio.service';
import { AlertItem, AlertReq, HistoryPoint, HoldingReq, PortfolioView } from '../../models/portfolio.model';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  private api = inject(PortfolioService);

  @ViewChild('allocCanvas') allocRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('histCanvas') histRef!: ElementRef<HTMLCanvasElement>;

  portfolio = signal<PortfolioView | null>(null);
  alerts = signal<AlertItem[]>([]);
  activeTicker = signal<string>('');

  newHolding: HoldingReq = { symbol: '', shares: 0, buyPrice: 0 };
  newAlert: AlertReq = { symbol: '', targetPrice: 0, direction: 'ABOVE', email: '' };

  private allocChart?: Chart;
  private histChart?: Chart;

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.api.getPortfolio().subscribe(data => {
      this.portfolio.set(data);
      this.renderAllocationChart(data);
      if (data.holdings.length > 0 && !this.activeTicker()) {
        this.selectTicker(data.holdings[0].symbol);
      }
    });
    this.api.getAlerts().subscribe(data => this.alerts.set(data));
  }

  saveHolding(): void {
    if (!this.newHolding.symbol || this.newHolding.shares <= 0) return;
    this.api.addHolding(this.newHolding).subscribe(() => {
      this.newHolding = { symbol: '', shares: 0, buyPrice: 0 };
      this.refresh();
    });
  }

  removeHolding(id: number): void {
    this.api.deleteHolding(id).subscribe(() => this.refresh());
  }

  saveAlert(): void {
    if (!this.newAlert.symbol || this.newAlert.targetPrice <= 0) return;
    this.api.addAlert(this.newAlert).subscribe(() => {
      this.newAlert = { symbol: '', targetPrice: 0, direction: 'ABOVE', email: '' };
      this.api.getAlerts().subscribe(data => this.alerts.set(data));
    });
  }

  removeAlert(id: number): void {
    this.api.deleteAlert(id).subscribe(() => {
      this.api.getAlerts().subscribe(data => this.alerts.set(data));
    });
  }

  selectTicker(sym: string): void {
    this.activeTicker.set(sym);
    this.api.getHistory(sym).subscribe(points => this.renderHistoryChart(sym, points));
  }

  private renderAllocationChart(data: PortfolioView): void {
    if (!this.allocRef?.nativeElement) return;
    if (this.allocChart) this.allocChart.destroy();

    const labels = data.holdings.map(h => h.symbol);
    const values = data.holdings.map(h => h.marketValue);

    this.allocChart = new Chart(this.allocRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels,
        datasets: [{
          data: values,
          backgroundColor: ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899']
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { position: 'bottom' } }
      }
    });
  }

  private renderHistoryChart(sym: string, points: HistoryPoint[]): void {
    if (!this.histRef?.nativeElement) return;
    if (this.histChart) this.histChart.destroy();

    const labels = points.map(p => p.date);
    const prices = points.map(p => p.close);

    this.histChart = new Chart(this.histRef.nativeElement, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: `${sym} Historical Close`,
          data: prices,
          borderColor: '#2563eb',
          backgroundColor: 'rgba(37, 99, 235, 0.1)',
          fill: true,
          tension: 0.2
        }]
      },
      options: {
        responsive: true,
        scales: {
          x: { display: false },
          y: { grid: { color: '#f3f4f6' } }
        }
      }
    });
  }
}