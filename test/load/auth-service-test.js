import http from "k6/http";
import { check, sleep, group } from "k6";
import { Rate, Trend, Counter, Gauge } from "k6/metrics";

const errorRate = new Rate("errors");
const requestDuration = new Trend("request_duration");
const loginDuration = new Trend("login_duration");
const registerDuration = new Trend("register_duration");
const profileDuration = new Trend("profile_duration");
const successfulLogins = new Counter("successful_logins");
const registrationCounter = new Counter("successful_registrations");

export const options = {
    stages: [
        // { duration: "1m", target: 200 },     // 300 RPS
        // { duration: "1m", target: 300 },     // 300 RPS
        // { duration: "1m", target: 400 },     // 400 RPS
        // { duration: "1m", target: 500 },     // 500 RPS
        // { duration: "1m", target: 750 },     // 750 RPS
        // { duration: "1m", target: 1000 },    // 1000 RPS
        { duration: "3m", target: 10000 },    // Держим 1000 RPS
        { duration: "1m", target: 0 },       // Снижение
    ],
    // // Total duration: 30s + 2m + 5m + 2m + 30s = 10m
    //
    // // Пороги качества
    // thresholds: {
    //     http_req_duration: ["p(95)<500", "p(99)<1000"], // 95% < 500ms, 99% < 1s
    //     errors: ["rate<0.05"], // Error rate < 5%
    //     login_duration: ["p(95)<300"], // Login < 300ms
    //     request_duration: ["avg<400"], // Средний < 400ms
    // },
};

const testUsers = [
    { username: "user1", password: "Password123!" },
    { username: "user2", password: "Password123!" },
    { username: "user3", password: "Password123!" },
];



const BASE_URL = 'http://192.168.1.109:8081';

export default function () {
    const user = testUsers[Math.floor(Math.random() * testUsers.length)];
    let authToken = "";

    group('User Authentication Flow', function () {
        const loginPayload = JSON.stringify({
            username: user.username,
            password: user.password,
        });

        const loginParams = {
            headers: {
                "Content-Type": "application/json",
            },
        };

        const startTime = new Date();
        const loginResponse = http.post(
            `${BASE_URL}/auth/login`,
            loginPayload,
            loginParams
        );

        const duration = new Date() - startTime;

        loginDuration.add(duration);
        requestDuration.add(duration);

        const loginSuccess = check(loginResponse, {
            "login status is 200": (r) => r.status === 200,
            "login response time < 300ms": (r) => r.timings.duration < 300,
            "has access_token": (r) => r.body.includes("accessToken"),
        });

        if (loginSuccess) {
            successfulLogins.add(1);
            // Парсим токен
            try {
                const loginData = JSON.parse(loginResponse.body);
                authToken = loginData.accessToken;
            } catch (e) {
                console.error(`Failed to parse login response: ${e}`);
            }
        } else {
            errorRate.add(1);
        }
    });

    sleep(1);

    if (authToken) {
        group("Get Profile", function () {
            const profileParams = {
                headers: {
                    Authorization: `Bearer ${authToken}`,
                    "Content-Type": "application/json",
                },
            };

            const startTime = new Date();
            const profileResponse = http.get(`${BASE_URL}/profile`, profileParams);
            const duration = new Date() - startTime;

            profileDuration.add(duration);
            requestDuration.add(duration);

            const profileSuccess = check(profileResponse, {
                "profile status is 200": (r) => r.status === 200,
                "profile response time < 200ms": (r) => r.timings.duration < 200,
                "has username": (r) => r.body.includes("username"),
            });

            if (!profileSuccess) {
                errorRate.add(1);
            }
        });

        sleep(1);

        // 3. CHANGE PASSWORD
        group("Change Password", function () {
            const newPassword = `Password${Math.random().toString().slice(2, 6)}!`;
            const changePasswordPayload = JSON.stringify({
                oldPassword: user.password,
                newPassword: newPassword,
            });

            const passwordParams = {
                headers: {
                    Authorization: `Bearer ${authToken}`,
                    "Content-Type": "application/json",
                },
            };

            const startTime = new Date();
            const changeResponse = http.put(
                `${BASE_URL}/profile/password`,
                changePasswordPayload,
                passwordParams
            );
            const duration = new Date() - startTime;

            requestDuration.add(duration);

            const changeSuccess = check(changeResponse, {
                "change password status is 200 or 204": (r) =>
                    r.status === 200 || r.status === 204,
            });

            if (!changeSuccess) {
                errorRate.add(1);
            }
        });
    }

    if (Math.random() < 0.2) {
        group("Registration", function () {
            const newUsername = `user_${Math.floor(Math.random() * 1000000)}`;
            const password = `Password123!${newUsername}`;
            const registerPayload = JSON.stringify({
                username: newUsername,
                email: `${newUsername}@test.com`,
                password: password,
                firstName: "Test",
                lastName: "User",
            });

            const registerParams = {
                headers: {
                    "Content-Type": "application/json",
                },
            };

            const startTime = new Date();
            const registerResponse = http.post(
                `${BASE_URL}/auth/register`,
                registerPayload,
                registerParams
            );
            const duration = new Date() - startTime;

            registerDuration.add(duration);
            requestDuration.add(duration);

            const registerSuccess = check(registerResponse, {
                "register status is 200 or 201": (r) =>
                    r.status === 200 || r.status === 201,
                "register response time < 500ms": (r) => r.timings.duration < 500,
            });

            if (registerSuccess) {
                registrationCounter.add(1);
                testUsers.push({
                    username: newUsername,
                    password: password,
                });
            } else {
                errorRate.add(1);
            }
        });
    }

    sleep(Math.random() * 3);
}

export function teardown(data) {
    console.log("Load test completed!");
}