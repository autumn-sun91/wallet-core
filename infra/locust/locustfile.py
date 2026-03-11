from locust import HttpUser, task, between
from random import randint
import uuid

class PointServiceUser(HttpUser):
    wait_time = between(0.001, 0.1)   # TPS 1000 목표

    # ─── 포인트 적립 ──────────────────────────────
    @task(6)
    def grant_point(self):
        user_id = randint(1, 1000)
        self.client.post(
            "/api/v1/points/grant",
            json={
                "userId":   user_id,
                "amount":   randint(1, 100),
                "type":     "QUESTION_REGISTER",
                "referKey": f"question-{uuid.uuid4()}",
            },
            name="/api/v1/points/grant",
        )

    # ─── 포인트 사용 ──────────────────────────────
    @task(2)
    def use_point(self):
        user_id = randint(1, 1000)
        self.client.post(
            "/api/v1/points/grant",
            json={
                "userId":   user_id,
                "amount":   randint(1, 10),
                "type":     "POINT_USE",
                "referKey": f"order-{uuid.uuid4()}",
            },
            name="/api/v1/points/use",
        )

    # ─── 잔액 조회 ────────────────────────────────
    @task(2)
    def get_balance(self):
        user_id = randint(1, 1000)
        self.client.get(
            f"/api/v1/points/{user_id}/balance",
            name="/api/v1/points/{userId}/balance",
        )