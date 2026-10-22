import cors from '@fastify/cors';
import Fastify from 'fastify';
import { readConfig, type Config } from './config.js';

// Construction is separate from listening so routes can be tested without a port.
export function buildApp(config: Config = readConfig()) {
  const app = Fastify({
    logger: config.logLevel === 'silent' ? false : { level: config.logLevel },
  });
  app.register(cors, { origin: config.corsOrigins, methods: ['GET'] });

  app.get('/health', {
    schema: {
      response: {
        200: {
          type: 'object', required: ['status'], additionalProperties: false,
          properties: { status: { type: 'string', const: 'ok' } },
        },
      },
    },
  }, async () => ({ status: 'ok' }));

  app.get('/api/v1/info', {
    schema: {
      response: {
        200: {
          type: 'object', required: ['name', 'version', 'message'], additionalProperties: false,
          properties: {
            name: { type: 'string' }, version: { type: 'string' }, message: { type: 'string' },
          },
        },
      },
    },
  }, async () => ({
    name: 'Chano', version: '0.1.0', message: 'Your Chano backend is connected.',
  }));

  return app;
}
