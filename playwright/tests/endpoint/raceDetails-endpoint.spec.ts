import { test, expect } from '@playwright/test';

const rootEndpoint = "choose-your-fate/race-details"

test.describe("racedetails Get All endpoint", ()=> {
    
    test('Without token should return 403', async ({ request }) => {
        const response = await request.get(process.env.API_URL + rootEndpoint);
        expect(response.status()).toBe(403); //FORBIDDEN
    });

    test('With token of ADMIN should return list of all 100 seed data races or more.', async ({ request }) => {
        const response = await request.get(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                }
            }
        );
        expect(response.status()).toBe(200);
        const data: Array<{}> = await response.json();
        //There might have been created an extra race in another test which would break the test if it was expected to be the exact number.
        //Therefore we test for the data we know is there and expect that there might be more.
        expect(data.length).toBeGreaterThanOrEqual(100);

    });

    test('With token of USER should return list of all 100 seed data races or more.', async ({ request }) => {
        const response = await request.get(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );
        expect(response.status()).toBe(200);
        const data: Array<{}> = await response.json();
        //There might have been created an extra race in another test which would break the test if it was expected to be the exact number.
        //Therefore we test for the data we know is there and expect that there might be more.
        expect(data.length).toBeGreaterThanOrEqual(100);

    });
})

test.describe("racedetails Get by racedetails id endpoint", ()=> {
    let raceDetailsId = "/" + (Math.floor(Math.random() * 100) + 1);
    test('Without token should return 403', async ({ request }) => {
        const response = await request.get(process.env.API_URL + rootEndpoint +  raceDetailsId);
        expect(response.status()).toBe(403); //FORBIDDEN
    });

    test('With token of ADMIN should return 200 and data should not be null', async ({ request }) => {
        const response = await request.get(process.env.API_URL + rootEndpoint + raceDetailsId,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                }
            }
        );
        console.log(raceDetailsId)
        expect(response.status()).toBe(200);
        expect(await response.json()).not.toBeNull();
    });

    test('With token of USER should return list of all 100 seed data races or more.', async ({ request }) => {
        const response = await request.get(process.env.API_URL + rootEndpoint + raceDetailsId,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );

        expect(response.status()).toBe(200);
        expect(await response.json()).not.toBeNull();
    });

    test('With id that doesnt exist return status code 404', async ({ request }) => {

        const response = await request.get(process.env.API_URL + rootEndpoint + "/"+ 99999,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );

        expect(response.status()).toBe(404); //NOT FOUND
        expect(await response.json()).not.toBeNull();
    });

    test('With id that is null return status code 500', async ({ request }) => {
        const response = await request.get(process.env.API_URL + rootEndpoint + "/"+ null,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );

        expect(response.status()).toBe(500); //Internal Server Error
        expect(await response.json()).not.toBeNull();
    });
})

test.describe("racedetails create endpoint", ()=> {
    test('Without token should return 403', async ({ request }) => {
        const name = Math.random().toString(36).substring(2, 10);
        const response = await request.post(process.env.API_URL + rootEndpoint,
            {
                data: {
                    name: name,
                    startingChapterId: 1
                }
            }
        );

        expect(response.status()).toBe(403); //FORBIDDEN

        const verificationResponse = await request.get(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );
        expect(verificationResponse.status()).toBe(200);
        const data: Array<{id: number, name: string, startingChapterId: number}> = await verificationResponse.json();
        expect(
            data.some(item => item.name === name && item.startingChapterId == 1)
        ).toBe(false);
    });

    test('With token of ADMIN should return 200 and data should match the data sent', async ({ request }) => {
        const name = Math.random().toString(36).substring(2, 10);
        const response = await request.post(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                },
                data: {
                    name: name,
                    startingChapterId: 1
                }
            }
        );

        console.log(response)
        expect(response.status()).toBe(200);

        const verificationResponse = await request.get(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );
        expect(verificationResponse.status()).toBe(200);
        const data: Array<{id: number, name: string, startingChapterId: number}> = await verificationResponse.json();
        expect(
            data.some(item => item.name === name && item.startingChapterId == 1)
        ).toBe(true);
    });

    test('With token of USER should return 403 and data should not exist', async ({ request }) => {
        const name = Math.random().toString(36).substring(2, 10);
        const response = await request.post(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                },
                data: {
                    name: name,
                    startingChapterId: 1
                }
            }
        );

        expect(response.status()).toBe(403); //FORBIDDEN

        const verificationResponse = await request.get(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );
        expect(verificationResponse.status()).toBe(200);
        const data: Array<{id: number, name: string, startingChapterId: number}> = await verificationResponse.json();
        expect(
            data.some(item => item.name === name && item.startingChapterId == 1)
        ).toBe(false);
    });

    test('starting chapter is null Should return 500', async ({ request }) => {
        const name = Math.random().toString(36).substring(2, 10);
        const response = await request.post(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                },
                data: {
                    name: name,
                    startingChapterId: null
                }
            }
        );

        expect(response.status()).toBe(500);

        const verificationResponse = await request.get(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );
        expect(verificationResponse.status()).toBe(200);
        const data: Array<{id: number, name: string, startingChapterId: number}> = await verificationResponse.json();
        expect(
            data.some(item => item.name === name && item.startingChapterId == 1)
        ).toBe(false);
    });

    test('With startingChapterId doesnt exist return status code 404', async ({ request }) => {
        const name = Math.random().toString(36).substring(2, 10);
        const response = await request.post(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                },
                data: {
                    name: name,
                    startingChapterId: 99999
                }
            }
        );

        expect(response.status()).toBe(404); //NOT FOUND

        const verificationResponse = await request.get(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );
        expect(verificationResponse.status()).toBe(200);
        const data: Array<{id: number, name: string, startingChapterId: number}> = await verificationResponse.json();
        expect(
            data.some(item => item.name === name && item.startingChapterId == 1)
        ).toBe(false);
    });
})

test.describe("racedetails Delete by racedetails id endpoint", ()=> {
    let raceDetailsId = "/" + (Math.floor(Math.random() * 100) + 1);
    test('Without token should return 403', async ({ request }) => {
        const name = Math.random().toString(36).substring(2, 10);
        const createResponse = await request.post(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                },
                data: {
                    name: name,
                    startingChapterId: 1
                }
            }
        );
        const createdData: {id: number, name: string, startingChapterId: number} = await createResponse.json();

        const response = await request.delete(process.env.API_URL + rootEndpoint + "/"  + createdData.id);
        expect(response.status()).toBe(403); //FORBIDDEN

        const verificationResponse = await request.get(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );
        expect(verificationResponse.status()).toBe(200);
        const data: Array<{id: number, name: string, startingChapterId: number}> = await verificationResponse.json();
        expect(
            data.some(item => item.id === createdData.id && item.name === name && item.startingChapterId == 1)
        ).toBe(true);
    });

    test('With token of ADMIN should return 200 and created data should no longer exist', async ({ request }) => {
        const name = Math.random().toString(36).substring(2, 10);
        const createResponse = await request.post(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                },
                data: {
                    name: name,
                    startingChapterId: 1
                }
            }
        );
        const createdData: {id: number, name: string, startingChapterId: number} = await createResponse.json();

        const response = await request.delete(process.env.API_URL + rootEndpoint + "/" + createdData.id,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                }
            }
        );
        expect(response.status()).toBe(200);

        const verificationResponse = await request.get(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );
        expect(verificationResponse.status()).toBe(200);
        const data: Array<{id: number, name: string, startingChapterId: number}> = await verificationResponse.json();
        expect(
            data.some(item => item.id === createdData.id && item.name === name && item.startingChapterId == 1)
        ).toBe(false);
    });

    test('With token of USER should return 403 and created data should still exists', async ({ request }) => {
        const name = Math.random().toString(36).substring(2, 10);
        const createResponse = await request.post(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                },
                data: {
                    name: name,
                    startingChapterId: 1
                }
            }
        );
        const createdData: {id: number, name: string, startingChapterId: number} = await createResponse.json();

        const response = await request.delete(process.env.API_URL + rootEndpoint + "/" + createdData.id,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );
        expect(response.status()).toBe(403);

        const verificationResponse = await request.get(process.env.API_URL + rootEndpoint,
            {
                headers: {
                    Authorization: `Bearer ${process.env.USER_TOKEN}`
                }
            }
        );
        expect(verificationResponse.status()).toBe(200);
        const data: Array<{id: number, name: string, startingChapterId: number}> = await verificationResponse.json();
        expect(
            data.some(item => item.id === createdData.id && item.name === name && item.startingChapterId == 1)
        ).toBe(true);
    });

    test('With id that is null return status code 404', async ({ request }) => {
        const response = await request.delete(process.env.API_URL + rootEndpoint + "/" + 9999,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                }
            }
        );
        expect(response.status()).toBe(404);
    });

    test('With id that is null return status code 500', async ({ request }) => {

        const response = await request.delete(process.env.API_URL + rootEndpoint + "/" + null,
            {
                headers: {
                    Authorization: `Bearer ${process.env.ADMIN_TOKEN}`
                }
            }
        );
        expect(response.status()).toBe(500);
    });
})