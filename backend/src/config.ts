export interface Config {
  host: string;
  port: number;
  logLevel: 'fatal' | 'error' | 'warn' | 'info' | 'debug' | 'trace' | 'silent';
  corsOrigins: string[];
}

export function readConfig(env: NodeJS.ProcessEnv = process.env): Config {
  const port = Number(env.PORT ?? 3000);
  if (!Number.isInteger(port) || port < 1 || port > 65535) {
    throw new Error('PORT must be an integer between 1 and 65535');
  }
  const logLevel = env.LOG_LEVEL ?? 'info';
  const levels = ['fatal', 'error', 'warn', 'info', 'debug', 'trace', 'silent'];
  if (!levels.includes(logLevel)) throw new Error('Invalid LOG_LEVEL');
  return {
    host: env.HOST ?? '0.0.0.0',
    port,
    logLevel: logLevel as Config['logLevel'],
    corsOrigins: (env.CORS_ORIGINS ?? 'http://localhost:8080,http://127.0.0.1:8080')
      .split(',').map((origin) => origin.trim()).filter(Boolean),
  };
}
