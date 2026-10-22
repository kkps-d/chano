import assert from 'node:assert/strict';
import { test } from 'node:test';
import { buildApp } from '../src/app.js';
import { readConfig } from '../src/config.js';

test('health and versioned API return the client contract', async (t) => {
  const app = buildApp(readConfig({ LOG_LEVEL: 'silent' }));
  t.after(() => app.close());
  const health = await app.inject({ method: 'GET', url: '/health' });
  assert.equal(health.statusCode, 200);
  assert.deepEqual(health.json(), { status: 'ok' });
  const info = await app.inject({ method: 'GET', url: '/api/v1/info' });
  assert.equal(info.statusCode, 200);
  assert.deepEqual(info.json(), {
    name: 'Chano', version: '0.1.0', message: 'Your Chano backend is connected.',
  });
});

test('CORS allows configured web origins and excludes other origins', async (t) => {
  const app = buildApp(readConfig({ LOG_LEVEL: 'silent' }));
  t.after(() => app.close());
  for (const [origin, allowed] of [
    ['http://localhost:8080', true], ['https://untrusted.example', false],
  ] as const) {
    const response = await app.inject({ method: 'GET', url: '/api/v1/info', headers: { origin } });
    assert.equal(response.headers['access-control-allow-origin'], allowed ? origin : undefined);
  }
});

test('invalid environment configuration fails early', () => {
  for (const port of ['0', '-1', '65536', 'abc', '1.5', '']) {
    assert.throws(() => readConfig({ PORT: port }), /PORT/);
  }
  assert.throws(() => readConfig({ LOG_LEVEL: 'invalid' }), /LOG_LEVEL/);
});
