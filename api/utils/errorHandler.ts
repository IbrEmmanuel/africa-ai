import { NextResponse } from 'next/server';

export interface GlassmorphicErrorResponse {
  error: string;
  message: string;
  status: number;
  glassmorphism: {
    theme: 'error' | 'warning' | 'info';
    styleHint: string;
    borderGlowColor: string;
    backgroundColor: string;
    textColor: string;
    icon: string;
    title: string;
    actionLabel?: string;
  };
  details?: any;
}

export function handleGlassError(
  errorName: string,
  message: string,
  status: number,
  theme: 'error' | 'warning' | 'info' = 'error',
  details?: any
) {
  const themes = {
    error: {
      borderGlowColor: '#EF4444', // RoseRed / soft red
      backgroundColor: 'rgba(239, 68, 68, 0.15)',
      textColor: '#F8FAFC',
      icon: 'error_outline',
      title: 'CRITICAL NODE EXCEPTION'
    },
    warning: {
      borderGlowColor: '#F59E0B', // WarmAmber
      backgroundColor: 'rgba(245, 158, 11, 0.15)',
      textColor: '#F8FAFC',
      icon: 'warning',
      title: 'GATEWAY WARNING DETECTED'
    },
    info: {
      borderGlowColor: '#06B6D4', // NeonCyan
      backgroundColor: 'rgba(6, 182, 212, 0.15)',
      textColor: '#F8FAFC',
      icon: 'info',
      title: 'SYSTEM TELEMETRY INFO'
    }
  };

  const selectedTheme = themes[theme] || themes.error;

  const payload: GlassmorphicErrorResponse = {
    error: errorName,
    message,
    status,
    glassmorphism: {
      theme,
      styleHint: 'glassmorphism-blur-subtle-glow',
      borderGlowColor: selectedTheme.borderGlowColor,
      backgroundColor: selectedTheme.backgroundColor,
      textColor: selectedTheme.textColor,
      icon: selectedTheme.icon,
      title: selectedTheme.title,
    },
    details
  };

  return NextResponse.json(payload, { status });
}
