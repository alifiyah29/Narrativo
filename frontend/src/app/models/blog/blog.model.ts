export interface Blog {
    id: number;
    title: string;
    content: string;
    visibility: 'PUBLIC' | 'PRIVATE';
    author: {
        id: number;
        username: string;
    };
    createdAt: string;
    updatedAt: string;
}