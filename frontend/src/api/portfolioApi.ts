import api from './axios';

export interface HoldingSummary {
    id: string;
    tickerSymbol: string;
    assetType: 'STOCK' | 'ETF' | 'MUTUAL_FUND';
    quantity: number;
    purchasePrice: number;
    purchaseDate: string;
    sector: string;
    
    priceAvailable: boolean;
    currentPrice?: number;
    currentValue?: number;
    totalCostBasis?: number;
    absoluteGain?: number;
    percentageGain?: number;
}

export interface PortfolioSummary {
    totalCurrentValue: number;
    totalCostBasis: number;
    totalAbsoluteGain: number;
    totalPercentageGain: number;
    warningMessage?: string;
    holdings: HoldingSummary[];
}

export const getPortfolioSummary = async (): Promise<PortfolioSummary> => {
    const response = await api.get('/portfolio/summary');
    return response.data;
};
