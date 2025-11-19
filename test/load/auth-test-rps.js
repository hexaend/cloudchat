import http from "k6/http";
import { check } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";
import exec from "k6/execution";

const errorRate = new Rate("errors");
const requestDuration = new Trend("request_duration");
const successfulRequests = new Counter("successful_requests");

export const options = {
    stages: [
        { duration: "1m", target: 50 },      // 50 RPS
        { duration: "1m", target: 100 },     // 100 RPS
        { duration: "1m", target: 200 },     // 200 RPS
        { duration: "1m", target: 300 },     // 300 RPS
        { duration: "1m", target: 400 },     // 400 RPS
        { duration: "1m", target: 500 },     // 500 RPS
        { duration: "1m", target: 750 },     // 750 RPS
        { duration: "1m", target: 1000 },    // 1000 RPS
        { duration: "3m", target: 1000 },    // Держим 1000 RPS
        { duration: "1m", target: 0 },       // Снижение
    ],

    thresholds: {
        http_req_duration: ["p(95)<1000"],
        errors: ["rate<0.05"],
    },
};

const testUsers = [
    { username: "user1", password: "Password123!" },
    { username: "user2", password: "Password123!" },
    { username: "user3", password: "Password123!" },
];

const BASE_URL = 'http://192.168.1.109:8081';

export default function () {
    const user = testUsers[Math.floor(Math.random() * testUsers.length)];

    const loginPayload = JSON.stringify({
        username: user.username,
        password: user.password,
    });

    const response = http.post(
        `${BASE_URL}/auth/login`,
        loginPayload,
        {
            headers: { "Content-Type": "application/json" },
            timeout: "10s",
        }
    );

    const success = check(response, {
        "status is 200": (r) => r.status === 200,
        "response time < 1s": (r) => r.timings.duration < 1000,
    });

    if (success) {
        successfulRequests.add(1);
    } else {
        errorRate.add(1);
    }

    requestDuration.add(response.timings.duration);
}

export function teardown(data) {
    console.log("Aggressive RPS test completed!");
}